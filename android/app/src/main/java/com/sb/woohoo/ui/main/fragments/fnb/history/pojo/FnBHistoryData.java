package com.sb.woohoo.ui.main.fragments.fnb.history.pojo;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;

public class FnBHistoryData {

    @SerializedName("id")
    @Expose
    private int id;

    @SerializedName("amount")
    @Expose
    private String amount;

    @SerializedName("currency")
    @Expose
    private String currency;

    @SerializedName("currentStatus")
    @Expose
    private String currentStatus;

    public String getCreatedAt() {
        return createdAt;
    }

    @SerializedName("createdAt")
    @Expose
    private String createdAt;

    @SerializedName("items")
    @Expose
    ArrayList<Items> items;

    public int getId() {
        return id;
    }

    public String getAmount() {
        return amount;
    }

    public String getCurrency() {
        return currency;
    }

    public String getCurrentStatus() {
        return currentStatus;
    }

    public ArrayList<Items> getItems() {
        return items;
    }

    public class Items {

        public String getPrice() {
            return price;
        }

        public String getCurrency() {
            return currency;
        }

        public String getName() {
            return name;
        }

        public String getQuantity() {
            return quantity;
        }

        @SerializedName("price")
        @Expose
        private String price;

        @SerializedName("currency")
        @Expose
        private String currency;

        @SerializedName("name")
        @Expose
        private String name;

        @SerializedName("quantity")
        @Expose
        private String quantity;

        public String getDescription() {
            return description;
        }

        @SerializedName("description")
        @Expose
        private String description;

        public String getInstructions() {
            return instructions;
        }

        @SerializedName("instructions")
        @Expose
        private String instructions;
    }

    /*{
            "id": 343,
            "orderId": "3jn4ku7d4l8zs2hthhjk3j",
            "amount": "343",
            "currency": "$",
            "items": [
                {
                    "amount": "210",
                    "categoryId": 9,
                    "createdAt": "2020-08-17T14:58:31.644Z",
                    "currency": "$",
                    "description": "Tomato Soup",
                    "domainId": 14,
                    "id": 13,
                    "images": [],
                    "instructions": "",
                    "name": "Tomato Soup",
                    "price": "70",
                    "quantity": 3,
                    "stock": 188,
                    "subCategoryId": 12,
                    "tags": [],
                    "updatedAt": "2021-04-01T06:18:44.115Z"
                }
            ],
            "statusTrack": [
                {
                    "status": "pending",
                    "timeStamp": "2021-05-02T15:11:42.867Z"
                }
            ],
            "currentStatus": "pending",
            "deliveryDate": null,
            "paymentMethod": "",
            "createdAt": "2021-05-02T15:11:42.867Z",
            "updatedAt": "2021-05-02T15:11:42.867Z",
            "userId": 461,
            "hubId": "f3cb17592055a8d0",
            "groupId": null,
            "domainId": 14,
            "roomId": null,
            "propertyId": 126,
            "room": null,
            "propertyDetail": {
                "id": 126,
                "name": "new tower",
                "location": "new location",
                "timeZone": "GMT+05:30 Asia/Kolkata",
                "logoUrl": "https://s3-us-west-2.amazonaws.com/woohoo-enterprise/logoImages/upload_bb212b966b3fd662b39c154ebd9bd35b.jpg",
                "backgroundImageUrl": "https://s3-us-west-2.amazonaws.com/woohoo-enterprise/backgroundImages/upload_bd3bf5dc79175071bd46ae305dcca244.jpg",
                "tabletBackgroundImageUrl": null,
                "impalaHotelID": "",
                "qrCodeUrl": "https://s3-us-west-2.amazonaws.com/woohoo-enterprise/fnbImagesFolder/14_126_qrcode.png",
                "createdAt": "2021-02-03T19:46:03.484Z",
                "updatedAt": "2021-04-23T11:29:46.927Z",
                "deletedAt": null,
                "domainId": 14
            }
        }*/
}
