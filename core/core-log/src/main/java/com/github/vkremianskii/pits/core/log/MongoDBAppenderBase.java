package com.github.vkremianskii.pits.core.log;

import ch.qos.logback.core.UnsynchronizedAppenderBase;
import com.mongodb.reactivestreams.client.MongoClient;
import com.mongodb.reactivestreams.client.MongoClients;
import com.mongodb.reactivestreams.client.MongoCollection;
import org.bson.Document;
import reactor.core.publisher.Mono;

public abstract class MongoDBAppenderBase<E> extends UnsynchronizedAppenderBase<E> {

    private String uri;
    private String database;
    private String collection;

    private MongoClient mongoClient;
    private MongoCollection<Document> eventsCollection;

    @Override
    public void start() {
        if (uri == null) {
            addError("Invalid MongoDB URI.");
            return;
        }
        if (database == null) {
            addError("Invalid MongoDB database.");
            return;
        }
        if (collection == null) {
            addError("Invalid MongoDB collection.");
            return;
        }
        mongoClient = MongoClients.create(uri);
        eventsCollection = mongoClient.getDatabase(database).getCollection(collection);
        super.start();
    }

    @Override
    protected void append(E event) {
        final var document = toMongoDocument(event);
        Mono.from(eventsCollection.insertOne(document))
            .onErrorResume(ignored -> {
                System.out.println(ignored.getMessage());
                return Mono.empty();
            })
            .subscribe();
    }

    protected abstract Document toMongoDocument(E event);

    @Override
    public void stop() {
        if (mongoClient != null) {
            mongoClient.close();
        }
        super.stop();
    }

    public void setUri(String uri) {
        this.uri = uri;
    }

    public void setDatabase(String database) {
        this.database = database;
    }

    public void setCollection(String collection) {
        this.collection = collection;
    }
}
