package io.github.wulkanowy.api.messages;

import com.google.gson.Gson;
import com.google.gson.JsonParseException;

import java.io.IOException;
import java.util.List;

import io.github.wulkanowy.api.Client;
import io.github.wulkanowy.api.NotLoggedInErrorException;
import io.github.wulkanowy.api.VulcanException;

public class Messages {

    private static final String BASE_URL = "{schema}://uonetplus-uzytkownik.{host}/{symbol}/";

    private static final String LIST_BASE_URL = BASE_URL + "Wiadomosc.mvc/";

    private static final String RECEIVED_URL = LIST_BASE_URL + "GetWiadomosciOdebrane";

    private static final String SENT_URL = LIST_BASE_URL + "GetWiadomosciWyslane";

    private static final String DELETED_URL = LIST_BASE_URL + "GetWiadomosciUsuniete";

    private static final String MESSAGE_URL = LIST_BASE_URL + "GetTrescWiadomosci";

    public static final int RECEIVED_FOLDER = 1;

    public static final int SENT_FOLDER = 2;

    public static final int DELETED_FOLDER = 3;

    private static final String ERROR_TITLE = "Błąd strony";

    private Client client;

    public Messages(Client client) {
        this.client = client;
    }

    public List<Message> getReceived() throws IOException, VulcanException {
        return getMessages(RECEIVED_URL);
    }

    public List<Message> getSent() throws IOException, VulcanException {
        return getMessages(SENT_URL);
    }

    public List<Message> getDeleted() throws IOException, VulcanException {
        return getMessages(DELETED_URL);
    }

    private List<Message> getMessages(String url) throws IOException, VulcanException {
        String res = client.getJsonStringByUrl(url);

        List<Message> messages;

        try {
            messages = new Gson().fromJson(res, MessagesContainer.class).data;
        } catch (JsonParseException e) {
            if (res.contains(ERROR_TITLE)) {
                throw new BadRequestException();
            }

            throw new NotLoggedInErrorException();
        }

        return messages;
    }

    public Message getMessage(int id, int folder) throws IOException, VulcanException {
        String res = client.postJsonStringByUrl(MESSAGE_URL, new String[][]{
                {"idWiadomosc", String.valueOf(id)},
                {"Folder", String.valueOf(folder)}
        });

        Message message;

        try {
            message = new Gson().fromJson(res, MessageContainer.class).data;
        } catch (JsonParseException e) {
            if (res.contains(ERROR_TITLE)) {
                throw new BadRequestException();
            }

            throw new NotLoggedInErrorException();
        }

        return message;
    }

    private static class MessagesContainer {
        private List<Message> data;
    }

    private static class MessageContainer {
        private Message data;
    }
}
