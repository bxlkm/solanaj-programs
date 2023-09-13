package com.mmorrell.openbook.program;

import com.mmorrell.openbook.OpenBookUtil;

import org.p2p.solanaj.core.Account;
import org.p2p.solanaj.core.AccountMeta;
import org.p2p.solanaj.core.PublicKey;
import org.p2p.solanaj.core.TransactionInstruction;
import org.p2p.solanaj.programs.Program;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Class for creating Serum v3 {@link TransactionInstruction}s
 */
public class OpenbookProgram extends Program {

    private static final PublicKey TOKEN_PROGRAM_ID =
            PublicKey.valueOf("TokenkegQfeZyiNwAJbNbGKPFXCWuBvf9Ss623VQ5DA");
    private static final PublicKey SYSVAR_RENT_PUBKEY =
            PublicKey.valueOf("SysvarRent111111111111111111111111111111111");
    public static final PublicKey OPENBOOK_V2_PROGRAM_ID = PublicKey.valueOf(
            "opnbkNkqux64GppQhwbyEVc3axhssFhVYuwar8rDHCu");

    private static final int MATCH_ORDERS_METHOD_ID = 2;
    private static final int CONSUME_EVENTS_METHOD_ID = 3;
    private static final int SETTLE_ORDERS_METHOD_ID = 5;
    private static final int CANCEL_ORDER_V2_METHOD_ID = 11;
    private static final int CANCEL_ORDER_BY_CLIENT_ID_V2_METHOD_ID = 12;

    // Open Book methods to implement
    // CreateOpenOrdersIndexer
    // aka create_open_orders_indexer
    // https://github.com/openbook-dex/openbook-v2/blob/master/programs/openbook-v2/src/accounts_ix/create_open_orders_indexer.rs

    public static TransactionInstruction createOpenOrdersIndexer(Account caller) {
        final List<AccountMeta> keys = new ArrayList<>();
        keys.add(new AccountMeta(caller.getPublicKey(),true, false));
        keys.add(new AccountMeta(caller.getPublicKey(),true, false));

        byte[] transactionData = OpenBookUtil.encodeNamespace("global:create_open_orders_indexer");

        return createTransactionInstruction(
                OPENBOOK_V2_PROGRAM_ID,
                keys,
                transactionData
        );
    }
}