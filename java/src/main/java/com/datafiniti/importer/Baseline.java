package com.datafiniti.importer;

import redis.clients.jedis.Jedis;
import java.io.IOException;

class Baseline {
    public static void run() throws IOException {
        ESClient.connect();
        ESClient.setup();

        seedRedis(10_000);

        importRecords();
    }

    public static void importRecords() throws IOException {
        Jedis jedis = new Jedis("redis");

        System.out.println("importing records from redis -> elasticsearch");

        String record;
        while ((record = jedis.rpop("records")) != null) {
            ESClient.insert(record);
        }

        System.out.println("done importing.");
    }

    public static void seedRedis(Integer numRecords) throws IOException {
        Jedis jedis = new Jedis("redis");

        System.out.println("flushing redis");
        jedis.flushAll();

        System.out.println("seeding redis with " + numRecords + " records.");
        for (int i = 0; i < numRecords; i += 1) {
            jedis.rpush("records", Utils.createRecord());

            if (i % (numRecords / 10) == 0) {
                System.out.println(i + " records seeded.");
            }
        }

        jedis.close();
    }
}