package org.hyperledger.fabric.samples.assettransfer;

import com.owlike.genson.annotation.JsonProperty;
import org.hyperledger.fabric.contract.annotation.DataType;
import org.hyperledger.fabric.contract.annotation.Property;

import java.util.Objects;

@DataType()
public class CampaignTransaction {

    @Property()
    private String id;

    @Property()
    private String dataRequestId;

    @Property()
    private String campaignId;

    @Property()
    private Double costPerImpression;

    @Property()
    private Double channelCostPerImpression;

    @Property()
    private Long intersectionDataCount;

    @Property()
    private String channel;

    @Property()
    private Double totalCost;

    @Property()
    private Double dataCost;

    @Property()
    private Double platformShare;

    @Property()
    private String dataProviderShare;

    @Property()
    private String dataProviderShareList;

    public String getId() { return id; }
    public String getDataRequestId() { return dataRequestId; }
    public String getCampaignId() { return campaignId; }
    public Double getCostPerImpression() { return costPerImpression; }
    public Double getChannelCostPerImpression() { return channelCostPerImpression; }
    public Long getIntersectionDataCount() { return intersectionDataCount; }
    public String getChannel() { return channel; }
    public Double getTotalCost() { return totalCost; }
    public Double getDataCost() { return dataCost; }
    public Double getPlatformShare() { return platformShare; }
    public String getDataProviderShare() { return dataProviderShare; }
    public String getDataProviderShareList() { return dataProviderShareList; }


    public CampaignTransaction(
            @JsonProperty("id") final String id,
            @JsonProperty("dataRequestId") final String dataRequestId,
            @JsonProperty("campaignId") final String campaignId,
            @JsonProperty("costPerImpression") final Double costPerImpression,
            @JsonProperty("channelCostPerImpression") final Double channelCostPerImpression,
            @JsonProperty("intersectionDataCount") final Long intersectionDataCount,
            @JsonProperty("channel") final String channel,
            @JsonProperty("totalCost") final Double totalCost,
            @JsonProperty("dataCost") final Double dataCost,
            @JsonProperty("platformShare") final Double platformShare,
            @JsonProperty("dataProviderShare") final String dataProviderShare,
            @JsonProperty("dataProviderShareList") final String dataProviderShareList) {

        this.id = id;
        this.dataRequestId = dataRequestId;
        this.campaignId = campaignId;
        this.costPerImpression = costPerImpression;
        this.channelCostPerImpression = channelCostPerImpression;
        this.intersectionDataCount = intersectionDataCount;
        this.channel = channel;
        this.totalCost = totalCost;
        this.dataCost = dataCost;
        this.platformShare = platformShare;
        this.dataProviderShare = dataProviderShare;
        this.dataProviderShareList = dataProviderShareList;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        CampaignTransaction that = (CampaignTransaction) o;
        return Objects.equals(id, that.id) &&
                Objects.equals(dataRequestId, that.dataRequestId) &&
                Objects.equals(campaignId, that.campaignId) &&
                Objects.equals(costPerImpression, that.costPerImpression) &&
                Objects.equals(channelCostPerImpression, that.channelCostPerImpression) &&
                Objects.equals(intersectionDataCount, that.intersectionDataCount) &&
                Objects.equals(channel, that.channel) &&
                Objects.equals(totalCost, that.totalCost) &&
                Objects.equals(dataCost, that.dataCost) &&
                Objects.equals(platformShare, that.platformShare) &&
                Objects.equals(dataProviderShare, that.dataProviderShare) &&
                Objects.equals(dataProviderShareList, that.dataProviderShareList);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, dataRequestId, campaignId, costPerImpression, channelCostPerImpression, intersectionDataCount, channel, totalCost, dataCost, platformShare, dataProviderShare, dataProviderShareList);
    }

    @Override
    public String toString() {
        return "CampaignTransaction{" +
                "id='" + id + '\'' +
                ", dataRequestId='" + dataRequestId + '\'' +
                ", campaignId='" + campaignId + '\'' +
                ", costPerImpression=" + costPerImpression +
                ", channelCostPerImpression=" + channelCostPerImpression +
                ", intersectionDataCount=" + intersectionDataCount +
                ", channel='" + channel + '\'' +
                ", totalCost=" + totalCost +
                ", dataCost=" + dataCost +
                ", platformShare=" + platformShare +
                ", dataProviderShare=" + dataProviderShare +
                ", dataProviderShareList='" + dataProviderShareList + '\'' +
                '}';
    }
}
