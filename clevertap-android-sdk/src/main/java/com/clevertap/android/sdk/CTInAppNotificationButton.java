package com.clevertap.android.sdk;


import android.os.Parcel;
import android.os.Parcelable;
import android.text.TextUtils;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Iterator;

class CTInAppNotificationButton implements Parcelable {

    private String text;
    private String textColor;
    private String backgroundColor;
    private String actionUrl;
    private JSONObject jsonDescription;
    private String error;
    private String borderColor;
    private String borderRadius;

    private HashMap<String, String> keyValues;

    CTInAppNotificationButton() {
    }

    String getText() {
        return text;
    }

    void setText(String text) {
        this.text = text;
    }

    String getTextColor() {
        return textColor;
    }

    @SuppressWarnings({"unused"})
    void setTextColor(String textColor) {
        this.textColor = textColor;
    }

    String getBackgroundColor() {
        return backgroundColor;
    }

    void setBackgroundColor(String backgroundColor) {
        this.backgroundColor = backgroundColor;
    }

    String getActionUrl() {
        return actionUrl;
    }

    @SuppressWarnings({"unused"})
    void setActionUrl(String actionUrl) {
        this.actionUrl = actionUrl;
    }

    @SuppressWarnings({"unused"})
    JSONObject getJsonDescription() {
        return jsonDescription;
    }

    @SuppressWarnings({"unused"})
    void setJsonDescription(JSONObject jsonDescription) {
        this.jsonDescription = jsonDescription;
    }

    String getError() {
        return error;
    }

    void setError(String error) {
        this.error = error;
    }

    String getBorderColor() {
        return borderColor;
    }

    @SuppressWarnings({"unused"})
    void setBorderColor(String borderColor) {
        this.borderColor = borderColor;
    }

    String getBorderRadius() {
        return borderRadius;
    }

    @SuppressWarnings({"unused"})
    void setBorderRadius(String borderRadius) {
        this.borderRadius = borderRadius;
    }

    @SuppressWarnings("unchecked")
    protected CTInAppNotificationButton(Parcel in) {
        text = in.readString();
        textColor = in.readString();
        backgroundColor = in.readString();
        actionUrl = in.readString();
        borderColor = in.readString();
        borderRadius = in.readString();

        try {
            jsonDescription = in.readByte() == 0x00 ? null : new JSONObject(in.readString());
        } catch (JSONException e) {
            e.printStackTrace();
        }
        error = in.readString();
        keyValues = in.readHashMap(null);
    }

    public HashMap<String, String> getKeyValues() {
        return keyValues;
    }

    CTInAppNotificationButton initWithJSON(JSONObject jsonObject) {
        try {
            this.jsonDescription = jsonObject;
            this.text = jsonObject.has(Constants.KEY_TEXT) ? jsonObject.getString(Constants.KEY_TEXT) : "";
            this.textColor = jsonObject.has(Constants.KEY_COLOR) ? jsonObject.getString(Constants.KEY_COLOR) : Constants.BLUE;
            this.backgroundColor = jsonObject.has(Constants.KEY_BG) ? jsonObject.getString(Constants.KEY_BG) : Constants.WHITE;
            this.borderColor = jsonObject.has(Constants.KEY_BORDER) ? jsonObject.getString(Constants.KEY_BORDER) : Constants.WHITE;
            this.borderRadius = jsonObject.has(Constants.KEY_RADIUS) ? jsonObject.getString(Constants.KEY_RADIUS) : "";

            JSONObject actions = jsonObject.has(Constants.KEY_ACTIONS) ? jsonObject.getJSONObject(Constants.KEY_ACTIONS) : null;
            if (actions != null) {
                String action = actions.has(Constants.KEY_ANDROID) ? actions.getString(Constants.KEY_ANDROID) : "";
                if (!action.isEmpty()) {
                    this.actionUrl = action;
                }
            }

            //Custom Key Value pairs
            if (isKVAction(actions)) {
                JSONObject keyValues = actions.getJSONObject(Constants.KEY_KV);
                if (keyValues != null) {
                    Iterator<String> keys = keyValues.keys();
                    if (keys != null) {
                        String key, value;
                        while (keys.hasNext()) {
                            key = keys.next();
                            value = keyValues.getString(key);
                            if (!TextUtils.isEmpty(key)) {
                                if (this.keyValues == null)
                                    this.keyValues = new HashMap<>();
                                this.keyValues.put(key, value);
                            }
                        }
                    }
                }
            }
        } catch (JSONException e) {
            this.error = "Invalid JSON";
        }
        return this;
    }

    /**
     * Checks if custom Key Value pair is present or not
     * @param actions - action object in the payload
     * @return
     * @throws JSONException
     */
    private boolean isKVAction(JSONObject actions) throws JSONException {
        return actions != null && actions.has(Constants.KEY_TYPE) && Constants.KEY_KV.equalsIgnoreCase(actions.getString(Constants.KEY_TYPE)) && actions.has(Constants.KEY_KV);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(text);
        dest.writeString(textColor);
        dest.writeString(backgroundColor);
        dest.writeString(actionUrl);
        dest.writeString(borderColor);
        dest.writeString(borderRadius);

        if (jsonDescription == null) {
            dest.writeByte((byte) (0x00));
        } else {
            dest.writeByte((byte) (0x01));
            dest.writeString(jsonDescription.toString());
        }
        dest.writeString(error);
        dest.writeMap(keyValues);
    }

    @SuppressWarnings("unused")
    public static final Parcelable.Creator<CTInAppNotificationButton> CREATOR = new Parcelable.Creator<CTInAppNotificationButton>() {
        @Override
        public CTInAppNotificationButton createFromParcel(Parcel in) {
            return new CTInAppNotificationButton(in);
        }

        @Override
        public CTInAppNotificationButton[] newArray(int size) {
            return new CTInAppNotificationButton[size];
        }
    };
}
