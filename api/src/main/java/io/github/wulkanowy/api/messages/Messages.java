package io.github.wulkanowy.api.messages;

import com.google.gson.Gson;
import com.google.gson.JsonParseException;

import java.io.IOException;
import java.util.List;

import io.github.wulkanowy.api.Client;
import io.github.wulkanowy.api.login.NotLoggedInErrorException;

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

    private String schema;

    private String host;

    private String symbol;

    public Messages(Client client, String schema, String host, String symbol) {
        this.client = client;
        this.schema = schema;
        this.host = host;
        this.symbol = symbol;
    }

    private String getFilledUrl(String url) {
        return url.replace("{schema}", schema)
                .replace("{host}", host)
                .replace("{symbol}", symbol);
    }

    public List<Message> getReceived() throws IOException, NotLoggedInErrorException, BadRequestException {
        return getMessages(RECEIVED_URL);
    }

    public List<Message> getSent() throws IOException, NotLoggedInErrorException, BadRequestException {
        return getMessages(SENT_URL);
    }

    public List<Message> getDeleted() throws IOException, NotLoggedInErrorException, BadRequestException {
        return getMessages(DELETED_URL);
    }

    private List<Message> getMessages(String url) throws IOException, NotLoggedInErrorException, BadRequestException {
        String res = client.getJsonStringByUrl(getFilledUrl(url));

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

    public Message getMessage(int id, int folder) throws IOException, BadRequestException, NotLoggedInErrorException {
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

    public static class MessagesContainer {
        public List<Message> data;
    }

    public static class MessageContainer {
        public Message data;
    }
}
