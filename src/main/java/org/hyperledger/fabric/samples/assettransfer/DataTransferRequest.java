/*
 * SPDX-License-Identifier: Apache-2.0
 */

package org.hyperledger.fabric.samples.assettransfer;

import java.util.*;


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
        name = "dataRequestBlock",
        info = @Info(
                title = "Asset Transfer",
                description = "The hyperlegendary asset transfer",
                version = "0.0.1-SNAPSHOT",
                license = @License(
                        name = "Apache 2.0 License",
                        url = "http://www.apache.org/licenses/LICENSE-2.0.html"),
                contact = @Contact(
                        email = "a.transfer@example.com",
                        name = "Data Transfer",
                        url = "https://hyperledger.example.com")))
@Default
public final class DataTransferRequest implements ContractInterface {

    private final Genson genson = new Genson();

    private enum DataTransferErrors {
        DATA_NOT_FOUND,
        DATA_ALREADY_EXISTS
    }

    /**
     * Creates some initial assets on the ledger.
     *
     * @param ctx the transaction context
     */
    @Transaction(intent = Transaction.TYPE.SUBMIT)
    public void InitLedger(final Context ctx) {
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
    public DataRequest CreateDataRequest(final Context ctx, final String requestId, final String description, final String createdOn,
                                         final String updatedOn, final String createdBy,
                                   final String owner, String attributeCodeList, String approvers,
                                   String campaignId, final Boolean deleted) {

        if (DataRequestExists(ctx, requestId)) {
            String errorMessage = String.format("Asset %s already exists", requestId);
            System.out.println(errorMessage);
            throw new ChaincodeException(errorMessage, DataTransferErrors.DATA_ALREADY_EXISTS.toString());
        }

        return putAsset(ctx, new DataRequest("data_" + requestId, description, createdOn, updatedOn, createdBy, owner, attributeCodeList, approvers, campaignId, deleted));
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
    public DataRequest FindByRequestId(final Context ctx, final String requestId) {
        String assetJSON = ctx.getStub().getStringState("data_" + requestId);

        if (assetJSON == null || assetJSON.isEmpty()) {
            String errorMessage = String.format("DataRequest %s does not exist", requestId);
            System.out.println(errorMessage);
            throw new ChaincodeException(errorMessage, DataTransferErrors.DATA_NOT_FOUND.toString());
        }

        return genson.deserialize(assetJSON, DataRequest.class);
    }

    @Transaction(intent = Transaction.TYPE.EVALUATE)
    public String FindByCampaignId(final Context ctx, final String campaignId) {
        List<DataRequest> matchingAssets = new ArrayList<>();

        // Use getStateByRange to iterate over all assets and filter by campaignId
        QueryResultsIterator<KeyValue> results = ctx.getStub().getStateByRange("data_", "data_\uFFFF");

        for (KeyValue result : results) {
            DataRequest asset = genson.deserialize(result.getStringValue(), DataRequest.class);

            // Check if the asset is not deleted and if the campaignId matches
            if (!Boolean.TRUE.equals(asset.getDeleted()) && asset.getCampaignId().equals(campaignId)) {
                matchingAssets.add(asset);
            }
        }

        if (matchingAssets.isEmpty()) {
            String errorMessage = String.format("No non-deleted assets found for Campaign ID %s", campaignId);
            System.out.println(errorMessage);
            throw new ChaincodeException(errorMessage, DataTransferErrors.DATA_NOT_FOUND.toString());
        }

        return genson.serialize(matchingAssets);
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
    public DataRequest UpdateDataRequest(final Context ctx, final String requestId, final String description, final String createdOn,
                                         final String updatedOn, final String createdBy,
                                   final String owner, String attributeCodeList, String approvers,
                                   String campaignId, final Boolean deleted) {

        if (!DataRequestExists(ctx, requestId)) {
            String errorMessage = String.format("Asset %s does not exist", requestId);
            System.out.println(errorMessage);
            throw new ChaincodeException(errorMessage, DataTransferErrors.DATA_NOT_FOUND.toString());
        }

        return putAsset(ctx, new DataRequest("data_" + requestId, description, createdOn, updatedOn, createdBy, owner, attributeCodeList, approvers, campaignId, deleted));
    }

    /**
     * Deletes asset on the ledger.
     *
     * @param ctx the transaction context
     * @param requestId the ID of the asset being deleted
     */
    @Transaction(intent = Transaction.TYPE.SUBMIT)
    public void DeleteDataRequest(final Context ctx, final String requestId) {
        if (!DataRequestExists(ctx, requestId)) {
            String errorMessage = String.format("Asset %s does not exist", requestId);
            System.out.println(errorMessage);
            throw new ChaincodeException(errorMessage, DataTransferErrors.DATA_NOT_FOUND.toString());
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
    public boolean DataRequestExists(final Context ctx, final String requestId) {
        String assetJSON = ctx.getStub().getStringState(addDataPrefixIfNotPresent(requestId));

        return (assetJSON != null && !assetJSON.isEmpty());
    }

    public static String addDataPrefixIfNotPresent(String input) {
        if (input != null && !input.startsWith("data_")) {
            return "data_" + input;
        }
        return input;
    }

    /**
     * Changes the owner of a asset on the ledger.
     *
     * @param ctx the transaction context
     * @param requestId the ID of the asset being transferred
     * @param newOwner the new owner
     * @return the old owner
     */

    /**
     * Retrieves all assets from the ledger.
     *
     * @param ctx the transaction context
     * @return array of assets found on the ledger
     */
    @Transaction(intent = Transaction.TYPE.EVALUATE)
    public String GetAllDataRequests(final Context ctx) {
        ChaincodeStub stub = ctx.getStub();

        List<DataRequest> queryResults = new ArrayList<>();

        // To retrieve all assets from the ledger use getStateByRange with empty startKey & endKey.
        // Giving empty startKey & endKey is interpreted as all the keys from beginning to end.
        // As another example, if you use startKey = 'asset0', endKey = 'asset9' ,
        // then getStateByRange will retrieve asset with keys between asset0 (inclusive) and asset9 (exclusive) in lexical order.
        QueryResultsIterator<KeyValue> results = stub.getStateByRange("data_", "data_\uFFFF");

        for (KeyValue result: results) {
            DataRequest dataRequest = genson.deserialize(result.getStringValue(), DataRequest.class);
            System.out.println(dataRequest);
            queryResults.add(dataRequest);
        }

        return genson.serialize(queryResults);
    }
}
