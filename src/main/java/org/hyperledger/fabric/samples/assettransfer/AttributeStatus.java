package org.hyperledger.fabric.samples.assettransfer;

public class AttributeStatus {

    private String name;
    private String orgId;
    private String status;

    public AttributeStatus(String name, String orgId, String status) {
        this.name = name;
        this.orgId = orgId;
        this.status = status;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getOrgId() {
        return orgId;
    }

    public void setOrgId(String orgId) {
        this.orgId = orgId;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    @Override
    public String toString() {
        return "AttributeStatus{" +
                "name='" + name + '\'' +
                ", orgId='" + orgId + '\'' +
                ", status='" + status + '\'' +
                '}';
    }
}
