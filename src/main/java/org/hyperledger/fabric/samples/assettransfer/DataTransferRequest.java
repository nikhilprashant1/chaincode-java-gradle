/*
 * SPDX-License-Identifier: Apache-2.0
 */

package org.hyperledger.fabric.samples.assettransfer;

import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.hyperledger.fabric.contract.*;
import org.hyperledger.fabric.contract.annotation.Contract;
import org.hyperledger.fabric.contract.annotation.Default;
import org.hyperledger.fabric.contract.annotation.Transaction;
import org.hyperledger.fabric.shim.*;
import org.hyperledger.fabric.shim.ledger.*;
import com.owlike.genson.Genson;

@Contract(name = "dataRequestBlock")
public final class DataTransferRequest implements ContractInterface {

    private final Genson genson = new Genson();

    private enum DataTransferErrors {
        DATA_NOT_FOUND,
        DATA_ALREADY_EXISTS,
        UNAUTHORIZED
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

        ClientIdentity clientIdentity = ctx.getClientIdentity();
        String mspId = clientIdentity.getMSPID();
        String peerCertSubject = clientIdentity.getX509Certificate().getSubjectX500Principal().getName();

        List<AttributeStatus> attributeStatuses = parseAttributeStatuses(attributeStatusList);

        Map<String, String> peerApprovalMap = new HashMap<>();
        peerApprovalMap.put("org1-peer1", "750ae4a4-5140-43c4-9e88-867ca57604d0");
        peerApprovalMap.put("org1-peer2", "e791a26f-dfa3-4644-8050-8f8917ce4e7d");

        String peerIdentityKey = extractPeerIdentity(peerCertSubject);

        for (AttributeStatus attrStatus : attributeStatuses) {
            if ("approved".equalsIgnoreCase(attrStatus.getStatus())) {
                String allowedOrgId = peerApprovalMap.get(peerIdentityKey);

                if (allowedOrgId == null || !allowedOrgId.equals(attrStatus.getOrgId())) {
                    throw new ChaincodeException("Unauthorized approval attempt for orgId " + attrStatus.getOrgId(),
                            DataTransferErrors.UNAUTHORIZED.toString());
                }
            }
        }

        return putAsset(ctx, new DataRequest(requestId, description, createdOn, updatedOn, createdBy, owner,
                attributeCodeList, attributeStatusList, approvers, campaignId,
                costPerImpression, deleted));
    }

    public static List<AttributeStatus> parseAttributeStatuses(String attributeStatusString) {
        List<AttributeStatus> attributeStatuses = new ArrayList<>();

        Pattern pattern = Pattern.compile(
                "AttributeStatus\\(name=(.*?), orgId=(.*?), status=(.*?)\\)");

        Matcher matcher = pattern.matcher(attributeStatusString);

        while (matcher.find()) {
            String name = matcher.group(1);
            String orgId = matcher.group(2);
            String status = matcher.group(3);

            AttributeStatus attributeStatus = new AttributeStatus(name, orgId, status);

            attributeStatuses.add(attributeStatus);
        }

        return attributeStatuses;
    }

    private String extractPeerIdentity(String certSubject) {
        if (certSubject.contains("CN=peer1")) {
            return "org1-peer1";
        } else if (certSubject.contains("CN=peer2")) {
            return "org1-peer2";
        }
        return "unknown-peer";
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
