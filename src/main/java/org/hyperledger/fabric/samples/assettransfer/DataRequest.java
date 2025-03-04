/*
 * SPDX-License-Identifier: Apache-2.0
 */

package org.hyperledger.fabric.samples.assettransfer;

import java.util.*;

import org.hyperledger.fabric.contract.annotation.DataType;
import org.hyperledger.fabric.contract.annotation.Property;

import com.owlike.genson.annotation.JsonProperty;

@DataType()
public final class DataRequest {

    @Property()
    private final String requestId;

    @Property()
    private final String description;

    @Property()
    private final String createdOn;

    @Property()
    private String updatedOn;

    @Property()
    private String createdBy;

    @Property()
    private final String owner;

    @Property()
    private String attributeCodeList;

    @Property()
    private String attributeStatusList;

    @Property()
    private String approvers;

    @Property()
    private String campaignId;

    @Property()
    private String costPerImpression;

    @Property()
    private final Boolean deleted;

    public static enum Status {
        approved, rejected, pending,deemedApproved, deemedRejected
    }

    public String getRequestId() {
        return requestId;
    }

    public String getDescription() {
        return description;
    }

    public String getCreatedOn() {
        return createdOn;
    }

    public String getUpdatedOn() {
        return updatedOn;
    }

    public String getCreatedBy() {
        return createdBy;
    }

    public String getOwner() {
        return owner;
    }

    public String getAttributeCodeList(){return attributeCodeList;}

    public String getAttributeStatusList(){return attributeStatusList;}

    public String getApprovers(){return approvers;}

    public String getCampaignId(){return campaignId;}

    public String getCostPerImpression(){return costPerImpression;}

    public Boolean getDeleted() {
        return deleted;
    }

    public DataRequest(@JsonProperty("requestId") final String requestId, @JsonProperty("description") final String description,
                       @JsonProperty("createdOn") final String createdOn, @JsonProperty("updatedOn") final String updatedOn,
                       @JsonProperty("createdBy") final String createdBy, @JsonProperty("owner") final String owner,
                       @JsonProperty("attributeCodeList") final String attributeCodeList, @JsonProperty("attributeStatusList") final String attributeStatusList,
                       @JsonProperty("approvers") final String approvers, @JsonProperty("campaignId") final String campaignId,
                       @JsonProperty("costPerImpression") final String costPerImpression, @JsonProperty("deleted") final Boolean deleted) {
        this.requestId = requestId;
        this.description = description;
        this.createdOn = createdOn;
        this.updatedOn = updatedOn;
        this.createdBy = createdBy;
        this.owner = owner;
        this.attributeCodeList = attributeCodeList;
        this.attributeStatusList = attributeStatusList;
        this.approvers = approvers;
        this.campaignId = campaignId;
        this.costPerImpression = costPerImpression;
        this.deleted = deleted;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        DataRequest that = (DataRequest) o;
        return Objects.equals(requestId, that.requestId) && Objects.equals(description, that.description) && Objects.equals(createdOn, that.createdOn) && Objects.equals(updatedOn, that.updatedOn) && Objects.equals(createdBy, that.createdBy) && Objects.equals(owner, that.owner) && Objects.equals(attributeCodeList, that.attributeCodeList) && Objects.equals(attributeStatusList, that.attributeStatusList) && Objects.equals(approvers, that.approvers) && Objects.equals(campaignId, that.campaignId) && Objects.equals(costPerImpression, that.costPerImpression) && Objects.equals(deleted, that.deleted);
    }

    @Override
    public int hashCode() {
        return Objects.hash(requestId, description, createdOn, updatedOn, createdBy, owner, attributeCodeList, attributeStatusList, approvers, campaignId, costPerImpression, deleted);
    }

    @Override
    public String toString() {
        return "DataRequest{" +
                "requestId='" + requestId + '\'' +
                ", description='" + description + '\'' +
                ", createdOn='" + createdOn + '\'' +
                ", updatedOn='" + updatedOn + '\'' +
                ", createdBy='" + createdBy + '\'' +
                ", owner='" + owner + '\'' +
                ", attributeCodeList='" + attributeCodeList + '\'' +
                ", attributeStatusList='" + attributeStatusList + '\'' +
                ", approvers='" + approvers + '\'' +
                ", campaignId='" + campaignId + '\'' +
                ", costPerImpression='" + costPerImpression + '\'' +
                ", deleted=" + deleted +
                '}';
    }

}