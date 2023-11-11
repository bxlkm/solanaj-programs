import com.google.common.io.Resources;
import com.mmorrell.phoenix.model.PhoenixMarketHeader;
import com.mmorrell.phoenix.program.PhoenixProgram;
import com.mmorrell.phoenix.program.PhoenixSeatManagerProgram;
import com.mmorrell.phoenix.util.Keccak;
import lombok.extern.slf4j.Slf4j;
import org.bitcoinj.core.Base58;
import org.junit.Test;
import org.p2p.solanaj.core.Account;
import org.p2p.solanaj.core.PublicKey;
import org.p2p.solanaj.core.Transaction;
import org.p2p.solanaj.programs.ComputeBudgetProgram;
import org.p2p.solanaj.rpc.Cluster;
import org.p2p.solanaj.rpc.RpcClient;
import org.p2p.solanaj.rpc.RpcException;
import org.p2p.solanaj.rpc.types.ProgramAccount;
import org.p2p.solanaj.rpc.types.config.Commitment;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.charset.Charset;
import java.util.Arrays;
import java.util.List;

@Slf4j
public class PhoenixTest {

    private final RpcClient client = new RpcClient("https://mainnet.helius-rpc.com/?api-key=a778b653-bdd6-41bc-8cda-0c7377faf1dd");
    private static final PublicKey SOL_USDC_MARKET = new PublicKey(
            "4DoNfFBfF7UokCC2FQzriy7yHK6DY6NVdYpuekQ5pRgg"
    );
    private static final PublicKey SOL_USDC_SEAT_MANAGER = new PublicKey(
            "JB3443UaUDA3z47AYdK4AUG8pgFgLfJVyyitHYkqC17L"
    );

    private static final PublicKey SOL_USDC_SEAT_DEPOSIT_COLLECTOR = new PublicKey(
            "DXECgdpNTDyXHGbyZfzLLriomtBvkXnBJipX3HgsyKXk"
    );

    @Test
    public void phoenixGetMarketsTest() throws RpcException {
        // GPA for all markets
        final List<ProgramAccount> markets = client.getApi().getProgramAccountsBase64(
                PhoenixProgram.PHOENIX_PROGRAM_ID,
                0,
                getDiscriminator("phoenix::program::accounts::MarketHeader")
        );

        System.out.println("Number of markets: " + markets.size());
        markets.forEach(programAccount -> {
            System.out.println("Market: " + programAccount.getPubkey());

            final PhoenixMarketHeader phoenixMarketHeader = PhoenixMarketHeader.readPhoenixMarketHeader(
                    Arrays.copyOfRange(
                            programAccount.getAccount().getDecodedData(),
                            0,
                            PhoenixMarketHeader.MARKET_HEADER_SIZE
                    )
            );
            System.out.println(phoenixMarketHeader);

        });
    }

    @Test
    public void phoenixClaimSeatTest() throws RpcException, IOException {
        Account tradingAccount = Account.fromJson(
                Resources.toString(
                        Resources.getResource(
                                "mikefsWLEcNYHgsiwSRr6PVd7yVcoKeaURQqeDE1tXN.json"),
                        Charset.defaultCharset()
                )
        );
        log.info("Trading account: {}", tradingAccount.getPublicKey().toBase58());

        // New Seat
        Account newSeatAccount = new Account();

        // Claim Seat
        Transaction claimSeatTransaction = new Transaction();

        claimSeatTransaction.addInstruction(
                ComputeBudgetProgram.setComputeUnitPrice(
                        1_000_000
                )
        );

        claimSeatTransaction.addInstruction(
                ComputeBudgetProgram.setComputeUnitLimit(
                        200_000
                )
        );

        claimSeatTransaction.addInstruction(
                PhoenixSeatManagerProgram.claimSeat(
                        SOL_USDC_MARKET,
                        SOL_USDC_SEAT_MANAGER,
                        SOL_USDC_SEAT_DEPOSIT_COLLECTOR,
                        tradingAccount.getPublicKey(),
                        tradingAccount.getPublicKey(),
                        newSeatAccount.getPublicKey()
                )
        );

        String claimSeatTxId =  client.getApi().sendTransaction(
                claimSeatTransaction,
                List.of(tradingAccount),
                client.getApi().getRecentBlockhash(Commitment.PROCESSED)
        );
        log.info("Claimed seat in transaction: {}", claimSeatTxId);
    }

    private String getDiscriminator(String input) {
        Keccak keccak = new Keccak(256);
        keccak.update(PhoenixProgram.PHOENIX_PROGRAM_ID.toByteArray());
        keccak.update(input.getBytes());

        ByteBuffer keccakBuffer = keccak.digest();
        keccakBuffer.order(ByteOrder.LITTLE_ENDIAN);
        byte[] keccakBytes = keccakBuffer.array();

        return Base58.encode(Arrays.copyOfRange(keccakBytes, 0, 8));
    }
}