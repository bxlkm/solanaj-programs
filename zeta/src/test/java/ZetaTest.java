import com.mmorrell.common.model.GenericOrderBook;
import com.mmorrell.common.model.Market;
import com.mmorrell.common.model.MarketBuilder;
import com.mmorrell.common.model.Order;
import com.mmorrell.common.model.OrderTypeLayout;
import com.mmorrell.common.model.SelfTradeBehaviorLayout;
import com.mmorrell.zeta.model.ZetaGroup;
import com.mmorrell.zeta.model.ZetaSide;
import com.mmorrell.zeta.program.ZetaProgram;
import lombok.extern.slf4j.Slf4j;
import org.bitcoinj.core.Base58;
import org.junit.Ignore;
import org.junit.Test;
import org.p2p.solanaj.core.Account;
import org.p2p.solanaj.core.PublicKey;
import org.p2p.solanaj.core.Transaction;
import org.p2p.solanaj.rpc.Cluster;
import org.p2p.solanaj.rpc.RpcClient;
import org.p2p.solanaj.rpc.RpcException;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Base64;

import static org.junit.Assert.assertNotNull;

@Slf4j
public class ZetaTest {

    private final RpcClient client = new RpcClient(Cluster.MAINNET);

    @Test
    public void zetaTest() throws RpcException {
        // Bids for market 21iZtRQWqBCBmq5oSCScpgU9JNEFLyEfWxdvpJxZuA8N
        PublicKey publicKey = new PublicKey("21iZtRQWqBCBmq5oSCScpgU9JNEFLyEfWxdvpJxZuA8N");
        System.out.println(publicKey.toBase58());

        byte[] data = Base64.getDecoder().decode(
                client.getApi().getAccountInfo(publicKey).getValue().getData().get(0)
        );

        Market zetaMarket = new MarketBuilder()
                .setClient(client)
                .setPublicKey(publicKey)
                .setDecimals(0, 6)
                .setRetrieveOrderBooks(true)
                .build();

        GenericOrderBook zetaOrderBook = zetaMarket.getBidOrderBook();

        zetaOrderBook.getOrders().forEach(genericOrder -> {
            System.out.println(genericOrder.toString());
        });

        GenericOrderBook zetaAskOrderBook = zetaMarket.getAskOrderBook();

        zetaAskOrderBook.getOrders().forEach(genericOrder -> {
            System.out.println(genericOrder.toString());
        });

        // Create account from private key
        Account account;
        try {
            account = Account.fromJson(Files.readString(Paths.get("src/main/resources/serummainnet.json")));
            log.info("Pubkey = " + account.getPublicKey());
        } catch (IOException e) {
            log.error(e.getMessage());
            e.printStackTrace();
        }

        // TODO - Add reverse lookup for OOA > Zeta account.
        // This requires going from Zeta > OOA, and matching, since OOA always has the same owner.
        // E.g. we need a function: getOwnerOfZetaOoa(ooaPubkey)
    }

    @Test
    public void zetaGroupTest() throws RpcException {
        PublicKey zetaGroupPubkey = new PublicKey("CoGhjFdyqzMFr5xVgznuBjULvoFbFtNN4bCdQzRArNK2");
        System.out.println(zetaGroupPubkey.toBase58());

        byte[] data = Base64.getDecoder().decode(
                client.getApi().getAccountInfo(zetaGroupPubkey).getValue().getData().get(0)
        );

        ZetaGroup zetaGroup = ZetaGroup.readZetaGroup(data);

        System.out.println(zetaGroup.toString());

        zetaGroup.getZetaProducts().forEach(zetaProduct -> {
            System.out.println(zetaProduct.toString());
        });
    }

    /**
     * Places a single bid.
     */
    @Test
    @Ignore
    public void zetaPlaceOrderTest() throws RpcException {
        Account account = null;
        try {
            account = Account.fromJson(Files.readString(Paths.get("src/test/resources/mainnet.json")));
            System.out.println("Pubkey = " + account.getPublicKey());
        } catch (IOException e) {
            System.out.println(e.getMessage());
            e.printStackTrace();
        }

        // quantity 420 = 0.420
        // price 141200 = 0.1412
        long orderId = 11133711L;
        final Order order = Order.builder()
                .price(133700)
                .quantity(420)
                .clientOrderId(orderId)
                .orderTypeLayout(OrderTypeLayout.LIMIT)
                .selfTradeBehaviorLayout(SelfTradeBehaviorLayout.DECREMENT_TAKE)
                .buy(true).build();

        PublicKey state = new PublicKey("8eExPiLp47xbSDYkbuem4qnLUpbLTfZBeFuEJoh6EUr2");
        PublicKey zetaGroup = new PublicKey("CoGhjFdyqzMFr5xVgznuBjULvoFbFtNN4bCdQzRArNK2");
        PublicKey marginAccount = new PublicKey("CqJuZ6iemaF2YhpLfKAi7k6qQA3Y2Lta78TtCxsTan7x");
        PublicKey serumAuthority = new PublicKey("AVNMK6wiGfppdQNg9WKfMRBXefDPGZFh2f3o1fRbgN8n");

        PublicKey greeks = new PublicKey("FRTCRjf8T5hFHZ9PKGPhYYVRWMFHKje4KwMAEttnDNBe");
        PublicKey openOrders = new PublicKey("w2Yf3EmttgmVM4ZwxEy9o7qJjTYrmjjhkWkhqn7NKkZ");

        Market nov11CallMarket = new MarketBuilder()
                .setPublicKey(new PublicKey("4LS1YmuTKby3LKFnMXEbsrUozNxcVC24qZDfi8ryEWHM"))
                .setClient(client)
                .build();

        PublicKey orderPayerTokenAccount = new PublicKey("HYstShC94QEE9bR7VtRVzVXPLLbSY7pBAgCsSNHwbFVT");
        PublicKey coinWallet = new PublicKey("6o6RCk1ogRL8yH4bDjmcb6pnBCRKkM7pxRjTDNynBb35");
        PublicKey pcWallet = new PublicKey("HYstShC94QEE9bR7VtRVzVXPLLbSY7pBAgCsSNHwbFVT");

        // Oracles
        PublicKey oracle = new PublicKey("H6ARHf6YXhGYeQfUzQNGk6rDNnLBQKrenN712K4AQJEG");
        PublicKey marketNode = new PublicKey("FGxQspeZqJZbgwryafN9oTPec2TDG5sRDehkyuwaFe6w");
        PublicKey marketMint = new PublicKey("J8UacxXk9orEDhZENQmp7fasHXaoyAXju2FDY8fqVMG9");
        PublicKey mintAuthority = new PublicKey("AV1UvTbycnqMe4JqHKGCqhACRd2m79YmtEUJrnCUQ3GT");

        Transaction transaction = new Transaction();
        transaction.addInstruction(
                ZetaProgram.placeOrder(
                        account,
                        state,
                        zetaGroup,
                        marginAccount,
                        serumAuthority,
                        greeks,
                        openOrders,
                        orderPayerTokenAccount,
                        coinWallet,
                        pcWallet,
                        oracle,
                        marketNode,
                        marketMint,
                        mintAuthority,
                        nov11CallMarket,
                        order,
                        ZetaSide.BID
                )
        );

        String transactionId = client.getApi().sendTransaction(transaction, account);
        System.out.println("Zeta placeOrder: " + transactionId);
    }
}
