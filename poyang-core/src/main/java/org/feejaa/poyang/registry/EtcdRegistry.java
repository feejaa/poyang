package org.feejaa.poyang.registry;

import io.etcd.jetcd.ByteSequence;
import io.etcd.jetcd.Client;
import io.etcd.jetcd.KV;
import io.etcd.jetcd.kv.GetResponse;
import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

@Slf4j
public class EtcdRegistry {

    public static void main(String[] args) {

        Client client = Client.builder().endpoints("http://localhost:2379").build();

        KV kvClient = client.getKVClient();
        ByteSequence key = ByteSequence.from("test_key".getBytes());
        ByteSequence value = ByteSequence.from("test_value".getBytes());

        kvClient.put(key, value);

        CompletableFuture<GetResponse> getResponseCompletableFuture = kvClient.get(key);

        try {
            GetResponse getResponse = getResponseCompletableFuture.get();
            log.info("resp:{}", getResponse);
        } catch (InterruptedException e) {


        } catch (ExecutionException e) {
            throw new RuntimeException(e);
        }

        kvClient.delete(key);

    }
}
