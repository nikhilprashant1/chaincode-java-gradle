package org.hyperledger.fabric.samples.assettransfer;

import java.util.ArrayList;
import java.util.List;

import org.hyperledger.fabric.contract.Context;
import org.hyperledger.fabric.contract.ContractInterface;
import org.hyperledger.fabric.contract.annotation.*;
import org.hyperledger.fabric.shim.ChaincodeException;
import org.hyperledger.fabric.shim.ledger.QueryResultsIterator;
import org.hyperledger.fabric.shim.ledger.KeyValue;

import com.owlike.genson.Genson;

@Contract(
        name = "notificationRequestBlock",
        info = @Info(
                title = "Notification Handler",
                description = "The hyperledger notification transfer",
                version = "0.0.1-SNAPSHOT"
        )
)
public final class UserNotificationHandler implements ContractInterface {

    private final Genson genson = new Genson();

    private enum AssetTransferErrors {
        NOTIFICATION_NOT_FOUND,
        NOTIFICATION_ALREADY_EXISTS
    }

    @Transaction(intent = Transaction.TYPE.SUBMIT)
    public UserNotification CreateUserNotification(
            final Context ctx,
            final String notificationId,
            final String requestId,
            final String campaignId,
            final String count,
            final String attributeList,
            final String owner,
            final String createdOn,
            final String message,
            final String status
    ) {
        if (NotificationExists(ctx, notificationId)) {
            throw new ChaincodeException("Notification already exists", AssetTransferErrors.NOTIFICATION_ALREADY_EXISTS.toString());
        }

        UserNotification notification = new UserNotification(notificationId, requestId, campaignId, count, attributeList, owner, createdOn, message, status);
        String notificationJson = genson.serialize(notification);
        ctx.getStub().putStringState(notificationId, notificationJson);

        return notification;
    }

    @Transaction(intent = Transaction.TYPE.EVALUATE)
    public String FindNotificationsByOwner(final Context ctx, final String owner) {
        String query = String.format("{\"selector\": {\"owner\": \"%s\"}}", owner);
        return executeQuery(ctx, query);
    }

    @Transaction(intent = Transaction.TYPE.EVALUATE)
    public String FindNotificationByRequestId(final Context ctx, final String requestId) {
        String query = String.format("{\"selector\": {\"requestId\": \"%s\"}}", requestId);
        return executeQuery(ctx, query);
    }

    @Transaction(intent = Transaction.TYPE.EVALUATE)
    public String FindNotificationByCampaignId(final Context ctx, final String campaignId) {
        String query = String.format("{\"selector\": {\"campaignId\": \"%s\"}}", campaignId);
        return executeQuery(ctx, query);
    }

    @Transaction(intent = Transaction.TYPE.EVALUATE)
    public String FindNotificationByOrgId(final Context ctx, final String orgId) {
        String query = String.format("{\"selector\": {\"owner\": \"%s\"}}", orgId);
        return executeQuery(ctx, query);
    }

    @Transaction(intent = Transaction.TYPE.EVALUATE)
    public String GetAllUserNotifications(final Context ctx) {
        String query = "{\"selector\": {}}";
        return executeQuery(ctx, query);
    }

    private String executeQuery(final Context ctx, String query) {
        QueryResultsIterator<KeyValue> results = ctx.getStub().getQueryResult(query);
        List<UserNotification> notifications = new ArrayList<>();

        for (KeyValue result : results) {
            UserNotification notification = genson.deserialize(result.getStringValue(), UserNotification.class);
            notifications.add(notification);
        }
        return genson.serialize(notifications);
    }

    @Transaction(intent = Transaction.TYPE.EVALUATE)
    public boolean NotificationExists(final Context ctx, final String notificationId) {
        byte[] notificationBytes = ctx.getStub().getState(notificationId);
        return notificationBytes != null && notificationBytes.length > 0;
    }
}