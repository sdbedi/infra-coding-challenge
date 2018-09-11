const Redis = require('ioredis');
const elasticsearch = require('./clients/elasticsearch');
const {performance} = require('perf_hooks');

const { createRecord, setupElasticsearch } = require('./utils');

(async () => {
    var total = 10000 //change this number to change the total to index - consider also making this a command line argument using process.argv
    await setupElasticsearch();
    await seedRedis(total);
    await importRecords(total);
    console.log("total time: ", performance.now()); //total execution time
})();

async function importRecords(numRecords) {
    console.log('importing records from redis -> elasticsearch');
    const redis = new Redis({ host: 'redis' });
    let record;
    let promiseArray = [];
    let arrayOfRequests = [];

    for (let i = 0; i < numRecords; i += 10000) {//we 
        promiseArray = [];
        arrayOfRequests = [];
        //we pipeline these retrieval requests - we don't wait for a response from one to start the next
        for (let k = 0; k < 10000; k ++) { 
            promiseArray.push(redis.rpop('records'));
        }
        await Promise.all(promiseArray);
        //we then use our results to build an array with the required format for the bulk api       
        promiseArray.forEach((rec) => {
            arrayOfRequests.push({"index": { _index: 'records', _type: 'all'}}, rec);
        })
        await elasticsearch.bulk({ body: arrayOfRequests}); //our bulk request
    }
        
    await redis.disconnect();
}

async function seedRedis(numRecords) {
    const redis = new Redis({ host: 'redis' });
    let recordsArray; //this will hold our batches   
    console.log('flushing redis');

    await redis.flushall();
    let firstPerformance = performance.now(); //save the timestamp for when we start seeding redis - we use it below
    /* I found that the fastest approach to seed redis was to pipeline push requests in batches (1000 was the ideal size on my machine)
    The default approach took at 14 seconds to seed redis; attempting to pipeline everything at once took roughly 3 minutes - I was able to reduce seed time to about 8 seconds 
     */
    for (var i = 0; i < numRecords; i += 1000) { 
        recordsArray = [];
        for (let k = 0; k < 1000; k ++) {
            recordsArray.push(redis.rpush('records', JSON.stringify(createRecord())));
        }
        await Promise.all(recordsArray); //we make a thousand requests without waiting for a response, then we wait for a response to all 1000
        if (i % (numRecords / 10) === 0) {
            console.log(`${i} records seeded in redis`);
        }
    }
    console.log('done seeding redis', performance.now() - firstPerformance); //this shows how long our seedRedis took
    await redis.disconnect();
}