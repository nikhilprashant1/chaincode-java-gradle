package org.hyperledger.fabric.samples.assettransfer;

import com.owlike.genson.annotation.JsonProperty;
import org.hyperledger.fabric.contract.annotation.DataType;
import org.hyperledger.fabric.contract.annotation.Property;

import java.util.Date;
import java.util.Objects;

@DataType
public class UserNotification {

    @Property()
    private final String id;

    @Property()
    private final String requestId;

    @Property()
    private final String campaignId;

    @Property()
    private final String count;

    @Property()
    private final String attributeList;

    @Property()
    private final String owner;

    @Property()
    private final Date createdOn;

    @Property()
    private final String message;

    @Property()
    private final String status;

    public String getId() {
        return id;
    }

    public String getRequestId() {
        return requestId;
    }

    public String getCampaignId() {
        return campaignId;
    }

    public String getCount() {
        return count;
    }

    public String getAttributeList() {
        return attributeList;
    }

    public String getOwner() {
        return owner;
    }

    public String getMessage() {
        return message;
    }

    public Date getCreatedOn() {
        return createdOn;
    }

    public String getStatus() {
        return status;
    }

    public UserNotification(
            @JsonProperty("id") final String id,
            @JsonProperty("requestId") final String requestId,
            @JsonProperty("campaignId") final String campaignId,
            @JsonProperty("count") final String count,
            @JsonProperty("attributeList") final String attributeList,
            @JsonProperty("owner") final String owner,
            @JsonProperty("createdOn") final Date createdOn,
            @JsonProperty("message") final String message,
            @JsonProperty("status") final String status) {
        this.id = id;
        this.requestId = requestId;
        this.campaignId = campaignId;
        this.count = count;
        this.attributeList = attributeList;
        this.owner = owner;
        this.createdOn = createdOn;
        this.message = message;
        this.status = status;
    }


    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        UserNotification that = (UserNotification) o;
        return count == that.count && Objects.equals(id, that.id) && Objects.equals(requestId, that.requestId) && Objects.equals(campaignId, that.campaignId) && Objects.equals(attributeList, that.attributeList) && Objects.equals(owner, that.owner) && Objects.equals(createdOn, that.createdOn) && Objects.equals(message, that.message) && status == that.status;
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, requestId, campaignId, count, attributeList, owner, createdOn, message, status);
    }

    @Override
    public String toString() {
        return "UserNotification{" +
                "id='" + id + '\'' +
                ", requestId='" + requestId + '\'' +
                ", campaignId='" + campaignId + '\'' +
                ", count=" + count +
                ", attributeList='" + attributeList + '\'' +
                ", owner='" + owner + '\'' +
                ", createdOn=" + createdOn +
                ", message='" + message + '\'' +
                ", status=" + status +
                '}';
    }

}
