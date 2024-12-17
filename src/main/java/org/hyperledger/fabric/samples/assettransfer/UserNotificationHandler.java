package org.hyperledger.fabric.samples.assettransfer;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.hyperledger.fabric.contract.Context;
import org.hyperledger.fabric.contract.ContractInterface;
import org.hyperledger.fabric.contract.annotation.*;
import org.hyperledger.fabric.shim.ChaincodeException;
import org.hyperledger.fabric.shim.ChaincodeStub;
import org.hyperledger.fabric.shim.ledger.KeyValue;
import org.hyperledger.fabric.shim.ledger.QueryResultsIterator;

import com.owlike.genson.Genson;

@Contract(
        name = "notificationRequestBlock",
        info = @Info(
                title = "Notification Handler",
                description = "The hyperlegendary notification transfer",
                version = "0.0.1-SNAPSHOT",
                license = @License(
                        name = "Apache 2.0 License",
                        url = "http://www.apache.org/licenses/LICENSE-2.0.html"),
                contact = @Contact(
                        email = "b.transfer@example.com",
                        name = "Notification Transfer",
                        url = "https://hyperledger.example.com")))
public final class UserNotificationHandler implements ContractInterface {

    private final Genson genson = new Genson();

    private enum AssetTransferErrors {
        NOTIFICATION_NOT_FOUND,
        NOTIFICATION_ALREADY_EXISTS
    }

    @Transaction(intent = Transaction.TYPE.SUBMIT)
    public void InitNotificationLedger(final Context ctx) {
    }

    @Transaction(intent = Transaction.TYPE.SUBMIT)
    public void CreateUserNotification(
            final Context ctx,
            final String notificationId,
            final String requestId,
            final String campaignId,
            final long count,
            final String attributeList,
            final String owner,
            final String message,
            final String status
    ) {
        if (NotificationExists(ctx, notificationId)) {
            String errorMessage = String.format("Notification %s already exists", notificationId);
            throw new ChaincodeException(errorMessage, AssetTransferErrors.NOTIFICATION_ALREADY_EXISTS.toString());
        }

        UserNotification notification = new UserNotification(notificationId, requestId, campaignId, count,
                attributeList, owner, new Date(), message, status
        );

        String notificationJSON = genson.serialize(notification);
        ctx.getStub().putState(notificationId, notificationJSON.getBytes(StandardCharsets.UTF_8));
    }


    @Transaction(intent = Transaction.TYPE.EVALUATE)
    public UserNotification ReadNotification(final Context ctx, final String notificationId) {
        byte[] notificationBytes = ctx.getStub().getState(notificationId);
        if (notificationBytes == null || notificationBytes.length == 0) {
            String errorMessage = String.format("Notification %s does not exist", notificationId);
            throw new ChaincodeException(errorMessage, AssetTransferErrors.NOTIFICATION_NOT_FOUND.toString());
        }

        String notificationJSON = new String(notificationBytes, StandardCharsets.UTF_8);
        return genson.deserialize(notificationJSON, UserNotification.class);
    }


    @Transaction(intent = Transaction.TYPE.SUBMIT)
    public UserNotification UpdateNotification(
            final Context ctx,
            final String notificationId,
            final String requestId,
            final String campaignId,
            final long count,
            final String attributeList,
            final String owner,
            final String message,
            final String status
    ) {
        if (!NotificationExists(ctx, notificationId)) {
            String errorMessage = String.format("Notification %s does not exist", notificationId);
            throw new ChaincodeException(errorMessage, AssetTransferErrors.NOTIFICATION_NOT_FOUND.toString());
        }

        UserNotification notification = ReadNotification(ctx, notificationId);

        String updatedNotificationJSON = genson.serialize(notification);
        return putAsset(ctx, new UserNotification(notificationId, requestId, campaignId, count,
                attributeList, owner, new Date(), message, status));
    }

    @Transaction(intent = Transaction.TYPE.EVALUATE)
    public String FindByOwner(final Context ctx, final String owner) {
        List<UserNotification> matchingAssets = new ArrayList<>();

        // Use getStateByRange to iterate over all assets and filter by campaignId
        QueryResultsIterator<KeyValue> results = ctx.getStub().getStateByRange("", "");

        for (KeyValue result : results) {
            UserNotification userNotification = genson.deserialize(result.getStringValue(), UserNotification.class);
            if (!Boolean.TRUE.equals(userNotification.getOwner().equals(owner))) {
                matchingAssets.add(userNotification);
            }
        }

        if (matchingAssets.isEmpty()) {
            String errorMessage = String.format("No non-deleted assets found for Owner %s", owner);
            System.out.println(errorMessage);
            throw new ChaincodeException(errorMessage, AssetTransferErrors.NOTIFICATION_NOT_FOUND.toString());
        }

        return genson.serialize(matchingAssets);
    }

    @Transaction(intent = Transaction.TYPE.EVALUATE)
    public UserNotification FindByCampaignId(final Context ctx, final String campaignId) {

        QueryResultsIterator<KeyValue> results = ctx.getStub().getStateByRange("", "");

        for (KeyValue result : results) {
            UserNotification userNotification = genson.deserialize(result.getStringValue(), UserNotification.class);
            if (!Boolean.TRUE.equals(userNotification.getCampaignId().equals(campaignId))) {
                return userNotification;
            }
        }

        return null;
    }

    @Transaction(intent = Transaction.TYPE.EVALUATE)
    public String FindByOrgId(final Context ctx, final String orgId) {
        List<UserNotification> matchingAssets = new ArrayList<>();

        QueryResultsIterator<KeyValue> results = ctx.getStub().getStateByRange("", "");

        for (KeyValue result : results) {
            UserNotification userNotification = genson.deserialize(result.getStringValue(), UserNotification.class);
            if (!Boolean.TRUE.equals(userNotification.getCampaignId().equals(orgId))) {
                matchingAssets.add(userNotification);
            }
        }

        if (matchingAssets.isEmpty()) {
            String errorMessage = String.format("No non-deleted assets found for Org Id %s", orgId);
            System.out.println(errorMessage);
            throw new ChaincodeException(errorMessage, AssetTransferErrors.NOTIFICATION_NOT_FOUND.toString());
        }

        return genson.serialize(matchingAssets);
    }

    private UserNotification putAsset(final Context ctx, final UserNotification userNotification) {
        // Use Genson to convert the Asset into string, sort it alphabetically and serialize it into a json string
        String sortedJson = genson.serialize(userNotification);
        ctx.getStub().putStringState(userNotification.getRequestId(), sortedJson);

        return userNotification;
    }

    @Transaction(intent = Transaction.TYPE.SUBMIT)
    public void DeleteNotification(final Context ctx, final String notificationId) {
        if (!NotificationExists(ctx, notificationId)) {
            String errorMessage = String.format("Notification %s does not exist", notificationId);
            throw new ChaincodeException(errorMessage, AssetTransferErrors.NOTIFICATION_NOT_FOUND.toString());
        }

        ctx.getStub().delState(notificationId);
    }

    @Transaction(intent = Transaction.TYPE.EVALUATE)
    public String FindNotificationsByOwner(final Context ctx, final String owner) {
        List<UserNotification> matchingNotifications = new ArrayList<>();
        QueryResultsIterator<KeyValue> results = ctx.getStub().getStateByRange("", "");

        for (KeyValue result : results) {
            UserNotification notification = genson.deserialize(result.getStringValue(), UserNotification.class);
            if (notification.getOwner().equals(owner)) {
                matchingNotifications.add(notification);
            }
        }

        return genson.serialize(matchingNotifications);
    }

    @Transaction(intent = Transaction.TYPE.EVALUATE)
    public String GetAllUserNotifications(final Context ctx) {
        ChaincodeStub stub = ctx.getStub();

        List<UserNotification> queryResults = new ArrayList<>();

        QueryResultsIterator<KeyValue> results = stub.getStateByRange("", "");

        for (KeyValue result : results) {
            UserNotification notification = genson.deserialize(result.getStringValue(), UserNotification.class);
            System.out.println(notification);
            queryResults.add(notification);
        }

        return genson.serialize(queryResults);
    }


    @Transaction(intent = Transaction.TYPE.EVALUATE)
    public boolean NotificationExists(final Context ctx, final String notificationId) {
        byte[] notificationBytes = ctx.getStub().getState(notificationId);
        return notificationBytes != null && notificationBytes.length > 0;
    }

}
