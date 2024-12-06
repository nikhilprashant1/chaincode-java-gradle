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
    private final String owner;

    @Property()
    private String attributeCodeList;

    @Property()
    private String approvers;

    @Property()
    private String campaignId;

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

    public String getOwner() {
        return owner;
    }

    public String getAttributeCodeList(){return attributeCodeList;}

    public String getApprovers(){return approvers;}

    public String getCampaignId(){return campaignId;}

    public Boolean getDeleted() {
        return deleted;
    }

    public DataRequest(@JsonProperty("requestId") final String requestId, @JsonProperty("description") final String description,
                       @JsonProperty("createdOn") final String createdOn, @JsonProperty("owner") final String owner,
                       @JsonProperty("attributeCodeList") final String attributeCodeList, @JsonProperty("approvers") final String approvers,
                       @JsonProperty("campaignId") final String campaignId, @JsonProperty("deleted") final Boolean deleted) {
        this.requestId = requestId;
        this.description = description;
        this.createdOn = createdOn;
        this.owner = owner;
        this.attributeCodeList = attributeCodeList;
        this.approvers = approvers;
        this.campaignId = campaignId;
        this.deleted = deleted;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        DataRequest that = (DataRequest) o;
        return deleted == that.deleted && Objects.equals(requestId, that.requestId) && Objects.equals(description, that.description) && Objects.equals(createdOn, that.createdOn) && Objects.equals(owner, that.owner) && Objects.equals(attributeCodeList, that.attributeCodeList) && Objects.equals(approvers, that.approvers) && Objects.equals(campaignId, that.campaignId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(requestId, description, createdOn, owner, attributeCodeList, approvers, campaignId, deleted);
    }

    @Override
    public String toString() {
        return "DataRequest{" +
                "requestId='" + requestId + '\'' +
                ", description='" + description + '\'' +
                ", createdOn=" + createdOn +
                ", owner='" + owner + '\'' +
                ", attributeCodeList=" + attributeCodeList +
                ", approvers=" + approvers +
                ", campaignId='" + campaignId + '\'' +
                ", deleted=" + deleted +
                '}';
    }
}
