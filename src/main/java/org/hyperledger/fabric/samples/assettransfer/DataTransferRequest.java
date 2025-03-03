/*
 * SPDX-License-Identifier: Apache-2.0
 */

package org.hyperledger.fabric.samples.assettransfer;

import java.util.*;
import org.hyperledger.fabric.contract.*;
import org.hyperledger.fabric.contract.annotation.Contract;
import org.hyperledger.fabric.contract.annotation.Default;
import org.hyperledger.fabric.contract.annotation.Transaction;
import org.hyperledger.fabric.shim.*;
import org.hyperledger.fabric.shim.ledger.*;
import com.owlike.genson.Genson;

@Contract(name = "dataRequestBlock")
@Default
public final class DataTransferRequest implements ContractInterface {

    private final Genson genson = new Genson();

    private enum DataTransferErrors {
        DATA_NOT_FOUND,
        DATA_ALREADY_EXISTS
    }

    @Transaction(intent = Transaction.TYPE.SUBMIT)
    public void InitLedger(final Context ctx) {}

    @Transaction(intent = Transaction.TYPE.SUBMIT)
    public DataRequest CreateDataRequest(final Context ctx, final String requestId, final String description, final String createdOn,
                                         final String updatedOn, final String createdBy, final String owner, String attributeCodeList,
                                         String attributeStatusList, String approvers, String campaignId, String costPerImpression, final Boolean deleted) {

        if (DataRequestExists(ctx, requestId)) {
            throw new ChaincodeException("Asset " + requestId + " already exists", DataTransferErrors.DATA_ALREADY_EXISTS.toString());
        }
        return putAsset(ctx, new DataRequest(requestId, description, createdOn, updatedOn, createdBy, owner, attributeCodeList, attributeStatusList, approvers, campaignId, costPerImpression, deleted));
    }

    private DataRequest putAsset(final Context ctx, final DataRequest dataRequest) {
        String json = genson.serialize(dataRequest);
        ctx.getStub().putStringState(dataRequest.getRequestId(), json);
        return dataRequest;
    }

    @Transaction(intent = Transaction.TYPE.EVALUATE)
    public DataRequest FindByRequestId(final Context ctx, final String requestId) {
        String assetJSON = ctx.getStub().getStringState(requestId);
        if (assetJSON == null || assetJSON.isEmpty()) {
            throw new ChaincodeException("DataRequest " + requestId + " does not exist", DataTransferErrors.DATA_NOT_FOUND.toString());
        }
        return genson.deserialize(assetJSON, DataRequest.class);
    }

    @Transaction(intent = Transaction.TYPE.EVALUATE)
    public String FindByCampaignId(final Context ctx, final String campaignId) {
        String queryString = String.format("{ \"selector\": { \"campaignId\": \"%s\", \"deleted\": false } }", campaignId);
        return executeQuery(ctx, queryString);
    }

    private String executeQuery(final Context ctx, String queryString) {
        QueryResultsIterator<KeyValue> results = ctx.getStub().getQueryResult(queryString);
        List<DataRequest> matchingAssets = new ArrayList<>();

        for (KeyValue result : results) {
            DataRequest asset = genson.deserialize(result.getStringValue(), DataRequest.class);
            matchingAssets.add(asset);
        }
        return genson.serialize(matchingAssets);
    }

    @Transaction(intent = Transaction.TYPE.SUBMIT)
    public DataRequest UpdateDataRequest(final Context ctx, final String requestId, final String description, final String createdOn,
                                         final String updatedOn, final String createdBy, final String owner, String attributeCodeList,
                                         String attributeStatusList, String approvers, String campaignId, String costPerImpression, final Boolean deleted) {

        if (!DataRequestExists(ctx, requestId)) {
            throw new ChaincodeException("Asset " + requestId + " does not exist", DataTransferErrors.DATA_NOT_FOUND.toString());
        }
        return putAsset(ctx, new DataRequest(requestId, description, createdOn, updatedOn, createdBy, owner, attributeCodeList, attributeStatusList, approvers, campaignId, costPerImpression, deleted));
    }

    @Transaction(intent = Transaction.TYPE.SUBMIT)
    public void DeleteDataRequest(final Context ctx, final String requestId) {
        if (!DataRequestExists(ctx, requestId)) {
            throw new ChaincodeException("Asset " + requestId + " does not exist", DataTransferErrors.DATA_NOT_FOUND.toString());
        }
        ctx.getStub().delState(requestId);
    }

    @Transaction(intent = Transaction.TYPE.EVALUATE)
    public boolean DataRequestExists(final Context ctx, final String requestId) {
        String assetJSON = ctx.getStub().getStringState(requestId);
        return (assetJSON != null && !assetJSON.isEmpty());
    }

    @Transaction(intent = Transaction.TYPE.EVALUATE)
    public String GetAllDataRequests(final Context ctx) {
        String queryString = "{ \"selector\": {} }";
        return executeQuery(ctx, queryString);
    }
}
