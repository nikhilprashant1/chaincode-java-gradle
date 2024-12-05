/*
 * SPDX-License-Identifier: Apache-2.0
 */

package org.hyperledger.fabric.samples.assettransfer;

import java.util.ArrayList;
import java.util.List;


import org.hyperledger.fabric.contract.Context;
import org.hyperledger.fabric.contract.ContractInterface;
import org.hyperledger.fabric.contract.annotation.Contact;
import org.hyperledger.fabric.contract.annotation.Contract;
import org.hyperledger.fabric.contract.annotation.Default;
import org.hyperledger.fabric.contract.annotation.Info;
import org.hyperledger.fabric.contract.annotation.License;
import org.hyperledger.fabric.contract.annotation.Transaction;
import org.hyperledger.fabric.shim.ChaincodeException;
import org.hyperledger.fabric.shim.ChaincodeStub;
import org.hyperledger.fabric.shim.ledger.KeyValue;
import org.hyperledger.fabric.shim.ledger.QueryResultsIterator;

import com.owlike.genson.Genson;

@Contract(
        name = "secondattempt",
        info = @Info(
                title = "Asset Transfer",
                description = "The hyperlegendary asset transfer",
                version = "0.0.1-SNAPSHOT",
                license = @License(
                        name = "Apache 2.0 License",
                        url = "http://www.apache.org/licenses/LICENSE-2.0.html"),
                contact = @Contact(
                        email = "a.transfer@example.com",
                        name = "Adrian Transfer",
                        url = "https://hyperledger.example.com")))
@Default
public final class DataTransferRequest implements ContractInterface {

    private final Genson genson = new Genson();

    private enum AssetTransferErrors {
        ASSET_NOT_FOUND,
        ASSET_ALREADY_EXISTS
    }

    /**
     * Creates some initial assets on the ledger.
     *
     * @param ctx the transaction context
     */
    @Transaction(intent = Transaction.TYPE.SUBMIT)
    public void InitLedger(final Context ctx) {
        putAsset(ctx, new DataRequest("asset1", "blue", 5L, "Tomoko", false));
        putAsset(ctx, new DataRequest("asset2", "red", 5L, "Brad", false));
        putAsset(ctx, new DataRequest("asset3", "green", 10L, "Jin Soo", false));
        putAsset(ctx, new DataRequest("asset4", "yellow", 10L, "Max", false));
        putAsset(ctx, new DataRequest("asset5", "black", 15L, "Adrian", false));
        putAsset(ctx, new DataRequest("asset6", "white", 15L, "Michel", false));

    }

    /**
     * Creates a new asset on the ledger.
     *
     * @param ctx the transaction context
     * @param requestId the ID of the new asset
     * @param description the description of the new asset
     * @param createdOn the createdOn for the new asset
     * @param owner the owner of the new asset
     * @param deleted the deleted of the new asset
     * @return the created asset
     */
    @Transaction(intent = Transaction.TYPE.SUBMIT)
    public DataRequest CreateAsset(final Context ctx, final String requestId, final String description, final Long createdOn,
                                   final String owner, final Boolean deleted) {

        if (AssetExists(ctx, requestId)) {
            String errorMessage = String.format("Asset %s already exists", requestId);
            System.out.println(errorMessage);
            throw new ChaincodeException(errorMessage, AssetTransferErrors.ASSET_ALREADY_EXISTS.toString());
        }

        return putAsset(ctx, new DataRequest(requestId, description, createdOn, owner, deleted));
    }

    private DataRequest putAsset(final Context ctx, final DataRequest dataRequest) {
        // Use Genson to convert the Asset into string, sort it alphabetically and serialize it into a json string
        String sortedJson = genson.serialize(dataRequest);
        ctx.getStub().putStringState(dataRequest.getRequestId(), sortedJson);

        return dataRequest;
    }

    /**
     * Retrieves an asset with the specified ID from the ledger.
     *
     * @param ctx the transaction context
     * @param requestId the ID of the asset
     * @return the asset found on the ledger if there was one
     */
    @Transaction(intent = Transaction.TYPE.EVALUATE)
    public DataRequest ReadAsset(final Context ctx, final String requestId) {
        String assetJSON = ctx.getStub().getStringState(requestId);

        if (assetJSON == null || assetJSON.isEmpty()) {
            String errorMessage = String.format("Asset %s does not exist", requestId);
            System.out.println(errorMessage);
            throw new ChaincodeException(errorMessage, AssetTransferErrors.ASSET_NOT_FOUND.toString());
        }

        return genson.deserialize(assetJSON, DataRequest.class);
    }

    /**
     * Updates the properties of an asset on the ledger.
     *
     * @param ctx the transaction context
     * @param requestId the ID of the asset being updated
     * @param description the description of the asset being updated
     * @param createdOn the createdOn of the asset being updated
     * @param owner the owner of the asset being updated
     * @param deleted the deleted of the asset being updated
     * @return the transferred asset
     */
    @Transaction(intent = Transaction.TYPE.SUBMIT)
    public DataRequest UpdateAsset(final Context ctx, final String requestId, final String description, final Long createdOn,
                                   final String owner, final Boolean deleted) {

        if (!AssetExists(ctx, requestId)) {
            String errorMessage = String.format("Asset %s does not exist", requestId);
            System.out.println(errorMessage);
            throw new ChaincodeException(errorMessage, AssetTransferErrors.ASSET_NOT_FOUND.toString());
        }

        return putAsset(ctx, new DataRequest(requestId, description, createdOn, owner, deleted));
    }

    /**
     * Deletes asset on the ledger.
     *
     * @param ctx the transaction context
     * @param requestId the ID of the asset being deleted
     */
    @Transaction(intent = Transaction.TYPE.SUBMIT)
    public void DeleteAsset(final Context ctx, final String requestId) {
        if (!AssetExists(ctx, requestId)) {
            String errorMessage = String.format("Asset %s does not exist", requestId);
            System.out.println(errorMessage);
            throw new ChaincodeException(errorMessage, AssetTransferErrors.ASSET_NOT_FOUND.toString());
        }

        ctx.getStub().delState(requestId);
    }

    /**
     * Checks the existence of the asset on the ledger
     *
     * @param ctx the transaction context
     * @param requestId the ID of the asset
     * @return boolean indicating the existence of the asset
     */
    @Transaction(intent = Transaction.TYPE.EVALUATE)
    public boolean AssetExists(final Context ctx, final String requestId) {
        String assetJSON = ctx.getStub().getStringState(requestId);

        return (assetJSON != null && !assetJSON.isEmpty());
    }

    /**
     * Changes the owner of a asset on the ledger.
     *
     * @param ctx the transaction context
     * @param requestId the ID of the asset being transferred
     * @param newOwner the new owner
     * @return the old owner
     */
    @Transaction(intent = Transaction.TYPE.SUBMIT)
    public String TransferAsset(final Context ctx, final String requestId, final String newOwner) {
        String assetJSON = ctx.getStub().getStringState(requestId);

        if (assetJSON == null || assetJSON.isEmpty()) {
            String errorMessage = String.format("Asset %s does not exist", requestId);
            System.out.println(errorMessage);
            throw new ChaincodeException(errorMessage, AssetTransferErrors.ASSET_NOT_FOUND.toString());
        }

        DataRequest dataRequest = genson.deserialize(assetJSON, DataRequest.class);

        putAsset(ctx, new DataRequest(dataRequest.getRequestId(), dataRequest.getDescription(), dataRequest.getCreatedOn(), newOwner, dataRequest.getDeleted()));

        return dataRequest.getOwner();
    }

    /**
     * Retrieves all assets from the ledger.
     *
     * @param ctx the transaction context
     * @return array of assets found on the ledger
     */
    @Transaction(intent = Transaction.TYPE.EVALUATE)
    public String GetAllAssets(final Context ctx) {
        ChaincodeStub stub = ctx.getStub();

        List<DataRequest> queryResults = new ArrayList<>();

        // To retrieve all assets from the ledger use getStateByRange with empty startKey & endKey.
        // Giving empty startKey & endKey is interpreted as all the keys from beginning to end.
        // As another example, if you use startKey = 'asset0', endKey = 'asset9' ,
        // then getStateByRange will retrieve asset with keys between asset0 (inclusive) and asset9 (exclusive) in lexical order.
        QueryResultsIterator<KeyValue> results = stub.getStateByRange("", "");

        for (KeyValue result: results) {
            DataRequest dataRequest = genson.deserialize(result.getStringValue(), DataRequest.class);
            System.out.println(dataRequest);
            queryResults.add(dataRequest);
        }

        return genson.serialize(queryResults);
    }
}
