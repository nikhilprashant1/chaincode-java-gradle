package org.hyperledger.fabric.samples.assettransfer;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
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
        // Initialize the ledger if needed
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
            final String status,
            final String userType,
            final String isRead
    ) {
        if (NotificationExists(ctx, notificationId)) {
            String errorMessage = String.format("Notification %s already exists", notificationId);
            throw new ChaincodeException(errorMessage, AssetTransferErrors.NOTIFICATION_ALREADY_EXISTS.toString());
        }

        UserNotification notification = new UserNotification("notification_" + notificationId, requestId, campaignId, count,
                attributeList, owner, createdOn, message, status,userType,isRead);

        String notificationJson = genson.serialize(notification);
        ctx.getStub().putStringState("notification_" + notificationId, notificationJson);

        return notification;
    }

    @Transaction(intent = Transaction.TYPE.EVALUATE)
    public UserNotification ReadNotification(final Context ctx, final String notificationId) {
        byte[] notificationBytes = ctx.getStub().getState("notification_" + notificationId);
        if (notificationBytes == null || notificationBytes.length == 0) {
            String errorMessage = String.format("Notification %s does not exist", notificationId);
            throw new ChaincodeException(errorMessage, AssetTransferErrors.NOTIFICATION_NOT_FOUND.toString());
        }

        return genson.deserialize(new String(notificationBytes, StandardCharsets.UTF_8), UserNotification.class);
    }

    @Transaction(intent = Transaction.TYPE.SUBMIT)
    public UserNotification UpdateNotification(
            final Context ctx,
            final String notificationId,
            final String requestId,
            final String campaignId,
            final String count,
            final String attributeList,
            final String owner,
            final String createdOn,
            final String message,
            final String status,
            final String userType,
            final String isRead
    ) {
        if (!NotificationExists(ctx, notificationId)) {
            String errorMessage = String.format("Notification %s does not exist", notificationId);
            throw new ChaincodeException(errorMessage, AssetTransferErrors.NOTIFICATION_NOT_FOUND.toString());
        }

        UserNotification notification = new UserNotification(addNotificationPrefixIfNotPresent(notificationId), requestId, campaignId, count,
                attributeList, owner, createdOn, message, status,userType,isRead);

        String notificationJson = genson.serialize(notification);
        ctx.getStub().putStringState(addNotificationPrefixIfNotPresent(notificationId), notificationJson);

        return notification;
    }

    @Transaction(intent = Transaction.TYPE.SUBMIT)
    public void DeleteNotification(final Context ctx, final String notificationId) {
        if (!NotificationExists(ctx, notificationId)) {
            String errorMessage = String.format("Notification %s does not exist", notificationId);
            throw new ChaincodeException(errorMessage, AssetTransferErrors.NOTIFICATION_NOT_FOUND.toString());
        }

        ctx.getStub().delState("notification_" + notificationId);
    }

    @Transaction(intent = Transaction.TYPE.EVALUATE)
    public UserNotification FindNotificationById(final Context ctx, final String id) {
        String assetJSON = ctx.getStub().getStringState(addNotificationPrefixIfNotPresent(id));

        if (assetJSON == null || assetJSON.isEmpty()) {
            String errorMessage = String.format("Notification %s does not exist", id);
            System.out.println(errorMessage);
            throw new ChaincodeException(errorMessage, AssetTransferErrors.NOTIFICATION_NOT_FOUND.toString());
        }

        return genson.deserialize(assetJSON, UserNotification.class);
    }

    @Transaction(intent = Transaction.TYPE.EVALUATE)
    public String FindNotificationsByOwner(final Context ctx, final String owner) {
        List<UserNotification> matchingNotifications = new ArrayList<>();
        QueryResultsIterator<KeyValue> results = ctx.getStub().getStateByRange("notification_", "notification_\uFFFF");

        for (KeyValue result : results) {
            UserNotification notification = genson.deserialize(result.getStringValue(), UserNotification.class);
            if (notification.getOwner().equals(owner)) {
                matchingNotifications.add(notification);
            }
        }

        return genson.serialize(matchingNotifications);
    }

    @Transaction(intent = Transaction.TYPE.EVALUATE)
    public String FindNotificationByRequestId(final Context ctx, final String requestId) {
        List<UserNotification> matchingAssets = new ArrayList<>();

        QueryResultsIterator<KeyValue> results = ctx.getStub().getStateByRange("notification_", "notification_\uFFFF");

        for (KeyValue result : results) {
            UserNotification userNotification = genson.deserialize(result.getStringValue(), UserNotification.class);
            if (Boolean.TRUE.equals(userNotification.getRequestId().equals(requestId))) {
                matchingAssets.add(userNotification);
            }
        }

        if (matchingAssets.isEmpty()) {
            String errorMessage = String.format("No non-deleted assets found for Request Id %s", requestId);
            System.out.println(errorMessage);
            throw new ChaincodeException(errorMessage, AssetTransferErrors.NOTIFICATION_NOT_FOUND.toString());
        }

        return genson.serialize(matchingAssets);
    }

    @Transaction(intent = Transaction.TYPE.EVALUATE)
    public String FindNotificationByCampaignId(final Context ctx, final String campaignId) {

        List<UserNotification> matchingAssets = new ArrayList<>();
        QueryResultsIterator<KeyValue> results = ctx.getStub().getStateByRange("", "");

        for (KeyValue result : results) {
            UserNotification userNotification = genson.deserialize(result.getStringValue(), UserNotification.class);
            if (Boolean.TRUE.equals(userNotification.getCampaignId().equals(campaignId))) {
                matchingAssets.add(userNotification);
            }
        }

        if (matchingAssets.isEmpty()) {
            String errorMessage = String.format("No non-deleted assets found for Campaign Id %s", campaignId);
            System.out.println(errorMessage);
            throw new ChaincodeException(errorMessage, AssetTransferErrors.NOTIFICATION_NOT_FOUND.toString());
        }

        return genson.serialize(matchingAssets);
    }

    @Transaction(intent = Transaction.TYPE.EVALUATE)
    public String FindNotificationByOrgId(final Context ctx, final String orgId) {
        List<UserNotification> matchingAssets = new ArrayList<>();

        QueryResultsIterator<KeyValue> results = ctx.getStub().getStateByRange("notification_", "notification_\uFFFF");

        for (KeyValue result : results) {
            UserNotification userNotification = genson.deserialize(result.getStringValue(), UserNotification.class);
            if (Boolean.TRUE.equals(userNotification.getOwner().equals(orgId))) {
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

    @Transaction(intent = Transaction.TYPE.EVALUATE)
    public String FindNotificationByOrgIdAndUserType(final Context ctx, final String orgId, final String userType) {
        List<UserNotification> matchingAssets = new ArrayList<>();

        QueryResultsIterator<KeyValue> results = ctx.getStub().getStateByRange("notification_", "notification_\uFFFF");

        for (KeyValue result : results) {
            UserNotification userNotification = genson.deserialize(result.getStringValue(), UserNotification.class);
            if (Boolean.TRUE.equals(userNotification.getOwner().equals(orgId))
                && Boolean.TRUE.equals(userNotification.getUserType())){
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
    @Transaction(intent = Transaction.TYPE.EVALUATE)
    public String CountNotificationByOrgIdAndUserType(final Context ctx, final String orgId, final String userType,final String isRead) {
        long matchingAssets=0;
        QueryResultsIterator<KeyValue> results = ctx.getStub().getStateByRange("notification_", "notification_\uFFFF");

        for (KeyValue result : results) {
            UserNotification userNotification = genson.deserialize(result.getStringValue(), UserNotification.class);
            if (Boolean.TRUE.equals(userNotification.getOwner().equals(orgId))
                    && Boolean.TRUE.equals(userNotification.getUserType().equals(userType))
                    &&Boolean.FALSE.equals(userNotification.getIsRead().equals(isRead))){
                matchingAssets++;
            }
        }

        if (matchingAssets==0) {
            String errorMessage = String.format("No non-deleted assets found for Org Id %s", orgId);
            System.out.println(errorMessage);
            throw new ChaincodeException(errorMessage, AssetTransferErrors.NOTIFICATION_NOT_FOUND.toString());
        }

        return genson.serialize(matchingAssets);
    }

    private UserNotification putAsset(final Context ctx, final UserNotification userNotification) {
        String sortedJson = genson.serialize(userNotification);
        ctx.getStub().putStringState(userNotification.getRequestId(), sortedJson);

        return userNotification;
    }

    @Transaction(intent = Transaction.TYPE.EVALUATE)
    public String GetAllUserNotifications(final Context ctx) {
        List<UserNotification> notifications = new ArrayList<>();
        QueryResultsIterator<KeyValue> results = ctx.getStub().getStateByRange("notification_", "notification_\uFFFF");

        for (KeyValue result : results) {
            UserNotification notification = genson.deserialize(result.getStringValue(), UserNotification.class);
            notifications.add(notification);
        }

        return genson.serialize(notifications);
    }

    @Transaction(intent = Transaction.TYPE.EVALUATE)
    public boolean NotificationExists(final Context ctx, final String notificationId) {
        byte[] notificationBytes = ctx.getStub().getState(addNotificationPrefixIfNotPresent("notification_" + notificationId));
        return notificationBytes != null && notificationBytes.length > 0;
    }

    public static String addNotificationPrefixIfNotPresent(String input) {
        if (input != null && !input.startsWith("notification_")) {
            return "notification_" + input;
        }
        return input;
    }
}
