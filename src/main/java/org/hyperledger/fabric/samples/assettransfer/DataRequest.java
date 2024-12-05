/*
 * SPDX-License-Identifier: Apache-2.0
 */

package org.hyperledger.fabric.samples.assettransfer;

import java.util.Objects;

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
    private final Long createdOn;

    @Property()
    private final String owner;

    @Property()
    private final boolean deleted;

    public String getRequestId() {
        return requestId;
    }

    public String getDescription() {
        return description;
    }

    public Long getCreatedOn() {
        return createdOn;
    }

    public String getOwner() {
        return owner;
    }

    public Boolean getDeleted() {
        return deleted;
    }

    public DataRequest(@JsonProperty("requestId") final String requestId, @JsonProperty("description") final String description,
                       @JsonProperty("createdOn") final Long createdOn, @JsonProperty("owner") final String owner,
                       @JsonProperty("deleted") final Boolean deleted) {
        this.requestId = requestId;
        this.description = description;
        this.createdOn = createdOn;
        this.owner = owner;
        this.deleted = deleted;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        DataRequest dataRequest = (DataRequest) o;
        return deleted == dataRequest.deleted && Objects.equals(requestId, dataRequest.requestId) && Objects.equals(description, dataRequest.description) && Objects.equals(createdOn, dataRequest.createdOn) && Objects.equals(owner, dataRequest.owner);
    }

    @Override
    public int hashCode() {
        return Objects.hash(requestId, description, createdOn, owner, deleted);
    }

    @Override
    public String toString() {
        return this.getClass().getSimpleName() + "@" + Integer.toHexString(hashCode()) + " [requestId=" + requestId + ", description="
                + description + ", createdOn=" + createdOn + ", owner=" + owner + ", deleted=" + deleted + "]";
    }
}
