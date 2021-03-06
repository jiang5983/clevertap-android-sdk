package com.clevertap.android.sdk;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.widget.Toast;

import org.json.JSONObject;

import java.util.HashMap;

/**
 * Custom OnClickListener to handle both "onMessage" and "onLink" clicks
 */
class CTInboxButtonClickListener implements View.OnClickListener {

    private int position;
    private CTInboxMessage inboxMessage;
    private String buttonText;
    private CTInboxListViewFragment fragment;
    private ViewPager viewPager;
    private JSONObject buttonObject;

    CTInboxButtonClickListener(int position, CTInboxMessage inboxMessage, String buttonText, JSONObject jsonObject, CTInboxListViewFragment fragment) {
        this.position = position;
        this.inboxMessage = inboxMessage;
        this.buttonText = buttonText;
        this.fragment = fragment; // be sure to pass this as a Weak Ref
        this.buttonObject = jsonObject;
    }

    CTInboxButtonClickListener(int position, CTInboxMessage inboxMessage, String buttonText, CTInboxListViewFragment fragment, ViewPager viewPager) {
        this.position = position;
        this.inboxMessage = inboxMessage;
        this.buttonText = buttonText;
        this.fragment = fragment; // be sure to pass this as a Weak Ref
        this.viewPager = viewPager;
    }


    @Override
    public void onClick(View v) {
        if (viewPager != null) {//Handles viewpager clicks
            if (fragment != null) {
                fragment.handleViewPagerClick(position, viewPager.getCurrentItem());
            }
        } else {//Handles button clicks
            if (buttonText != null && buttonObject != null) {
                if (fragment != null) {
                    if (inboxMessage.getInboxMessageContents().get(0).getLinktype(buttonObject).equalsIgnoreCase(Constants.COPY_TYPE)) {//Copy to clipboard feature
                        if (fragment.getActivity() != null) {
                            copyToClipboard(fragment.getActivity());
                        }
                    }

                    fragment.handleClick(this.position, buttonText, buttonObject, getKeyValues(inboxMessage));
                }
            } else {
                if (fragment != null) {
                    fragment.handleClick(this.position, null, null, null);
                }
            }
        }
    }

    /**
     * Returns Custom Key Value pairs if present in the payload
     * @param inboxMessage - InboxMessage object
     * @return HashMap<String, String>
     */
    private HashMap<String, String> getKeyValues(CTInboxMessage inboxMessage) {
        if (inboxMessage != null
                && inboxMessage.getInboxMessageContents() != null
                && inboxMessage.getInboxMessageContents().get(0) != null
                && Constants.KEY_KV.equalsIgnoreCase(inboxMessage.getInboxMessageContents().get(0).getLinktype(buttonObject))) {

            return inboxMessage.getInboxMessageContents().get(0).getLinkKeyValue(buttonObject);
        }
        return null;
    }

    private void copyToClipboard(Context context) {
        ClipboardManager clipboardManager = (ClipboardManager) context.getSystemService(Context.CLIPBOARD_SERVICE);
        ClipData clipData = ClipData.newPlainText(buttonText, inboxMessage.getInboxMessageContents().get(0).getLinkCopyText(buttonObject));
        if (clipboardManager != null) {
            clipboardManager.setPrimaryClip(clipData);
            Toast.makeText(context, "Text Copied to Clipboard", Toast.LENGTH_SHORT).show();
        }
    }
}
