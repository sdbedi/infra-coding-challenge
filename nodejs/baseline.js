const Redis = require('ioredis');
const elasticsearch = require('./clients/elasticsearch');

const { createRecord, setupElasticsearch } = require('./utils');

(async () => {
    await setupElasticsearch();
    await seedRedis();
    await importRecords();
})();

async function importRecords() {
    console.log('importing records from redis -> elasticsearch');
    
    const redis = new Redis({ host: 'redis' });
    let record;

    do {
        record = await redis.rpop('records');
        await elasticsearch.index({ index: 'records', type: 'all', body: record });
    } while (record);

    await redis.disconnect();
}

async function seedRedis() {
    const redis = new Redis({ host: 'redis' });
    
    console.log('flushing redis');
    await redis.flushall();
    
    for (let i = 0; i < 100000; i += 1) {
    
        await redis.rpush('records', JSON.stringify(createRecord()));

        if (i % 10000 === 0) {
            console.log(`${i} records seeded in redis`);
        }
    }

    console.log('done seeding redis');
    await redis.disconnect();
}