package org.hyperledger.fabric.samples.assettransfer;

import java.util.*;
import org.hyperledger.fabric.contract.Context;
import org.hyperledger.fabric.contract.ContractInterface;
import org.hyperledger.fabric.contract.annotation.*;
import org.hyperledger.fabric.shim.ChaincodeException;
import org.hyperledger.fabric.shim.ChaincodeStub;
import org.hyperledger.fabric.shim.ledger.KeyValue;
import org.hyperledger.fabric.shim.ledger.QueryResultsIterator;
import com.owlike.genson.Genson;

@Contract(
        name = "CostTransactionRequest",
        info = @Info(
                title = "Cost Transfer",
                description = "Handles cost transfer operations",
                version = "0.0.1-SNAPSHOT",
                license = @License(
                        name = "Apache 2.0 License",
                        url = "http://www.apache.org/licenses/LICENSE-2.0.html"),
                contact = @Contact(
                        email = "cost.transfer@example.com",
                        name = "Cost Transfer",
                        url = "https://hyperledger.example.com")))
public final class CostTransactionRequest implements ContractInterface {

    private final Genson genson = new Genson();

    private enum CostTransferErrors {
        COST_NOT_FOUND,
        COST_ALREADY_EXISTS
    }

    @Transaction(intent = Transaction.TYPE.SUBMIT)
    public void InitLedger(final Context ctx) {}

    @Transaction(intent = Transaction.TYPE.SUBMIT)
    public CampaignTransaction CreateCampaignTransaction(final Context ctx,
                                                         final String id,
                                                         final String dataRequestId,
                                                         final String campaignId,
                                                         final Double costPerImpression,
                                                         final Double channelCostPerImpression,
                                                         final Long intersectionDataCount,
                                                         final String channel,
                                                         final Double totalCost,
                                                         final Double dataCost,
                                                         final Double platformShare,
                                                         final String dataProviderShare,
                                                         final String dataProviderShareList) {

        if (CampaignTransactionExists(ctx, id)) {
            throw new ChaincodeException("Asset " + id + " already exists", "CAMPAIGN_COST_ALREADY_EXISTS");
        }

        return putAsset(ctx, new CampaignTransaction("cost_" + id, dataRequestId, campaignId,
                costPerImpression, channelCostPerImpression, intersectionDataCount, channel, totalCost,
                dataCost, platformShare, dataProviderShare, dataProviderShareList));
    }

    @Transaction(intent = Transaction.TYPE.EVALUATE)
    public boolean CampaignTransactionExists(final Context ctx, final String id) {
        String data = ctx.getStub().getStringState("cost_" + id);
        return (data != null && !data.isEmpty());
    }

    private CampaignTransaction putAsset(final Context ctx, final CampaignTransaction costTransfer) {
        String sortedJson = genson.serialize(costTransfer);
        ctx.getStub().putStringState(costTransfer.getId(), sortedJson);
        return costTransfer;
    }

    @Transaction(intent = Transaction.TYPE.EVALUATE)
    public CampaignTransaction FindByTransferId(final Context ctx, final String transferId) {
        String assetJSON = ctx.getStub().getStringState("cost_" + transferId);
        if (assetJSON == null || assetJSON.isEmpty()) {
            throw new ChaincodeException("Cost Transfer " + transferId + " does not exist", CostTransferErrors.COST_NOT_FOUND.toString());
        }
        return genson.deserialize(assetJSON, CampaignTransaction.class);
    }

    @Transaction(intent = Transaction.TYPE.SUBMIT)
    public CampaignTransaction UpdateCampaignTransaction(final Context ctx,
                                                         final String id,
                                                         final String dataRequestId,
                                                         final String campaignId,
                                                         final Double costPerImpression,
                                                         final Double channelCostPerImpression,
                                                         final Long intersectionDataCount,
                                                         final String channel,
                                                         final Double totalCost,
                                                         final Double dataCost,
                                                         final Double platformShare,
                                                         final String dataProviderShare,
                                                         final String dataProviderShareList) {

        if (!CampaignTransactionExists(ctx, id)) {
            throw new ChaincodeException("Asset " + id + " does not exist", "CAMPAIGN_COST_NOT_FOUND");
        }

        return putAsset(ctx, new CampaignTransaction("cost_" + id, dataRequestId, campaignId,
                costPerImpression, channelCostPerImpression, intersectionDataCount, channel,
                totalCost, dataCost, platformShare, dataProviderShare, dataProviderShareList));
    }

    @Transaction(intent = Transaction.TYPE.SUBMIT)
    public void DeleteCostTransfer(final Context ctx, final String transferId) {
        if (!CampaignTransactionExists(ctx, transferId)) {
            throw new ChaincodeException("Cost Transfer " + transferId + " does not exist", CostTransferErrors.COST_NOT_FOUND.toString());
        }
        ctx.getStub().delState("cost_" + transferId);
    }

    @Transaction(intent = Transaction.TYPE.EVALUATE)
    public String GetAllCostTransfers(final Context ctx) {
        ChaincodeStub stub = ctx.getStub();

        String query = "{ \"selector\": { \"id\": { \"$regex\": \"^cost_\" } } }";
        List<CampaignTransaction> queryResults = new ArrayList<>();
        QueryResultsIterator<KeyValue> results = stub.getQueryResult(query);

        for (KeyValue result : results) {
            CampaignTransaction costTransfer = genson.deserialize(result.getStringValue(), CampaignTransaction.class);
            queryResults.add(costTransfer);
        }

        return genson.serialize(queryResults);
    }
}