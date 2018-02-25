package io.github.wulkanowy.api.messages;

import com.google.gson.annotations.SerializedName;

public class Message {

    @SerializedName("Nieprzeczytana")
    public boolean unread;

    @SerializedName("Data")
    public String date;

    @SerializedName("Tresc")
    public String content;

    @SerializedName("Temat")
    public String subject;

    @SerializedName("NadawcaNazwa")
    public String sender;

    @SerializedName("IdWiadomosci")
    public int messageID;

    @SerializedName("IdNadawca")
    public int senderID;

    @SerializedName("Id")
    public int id;
}
