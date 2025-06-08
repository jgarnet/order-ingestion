const axios = require('axios');
const { faker } = require('@faker-js/faker');

const API_URL = 'http://localhost:8080/batch-orders';
const MAX_ORDERS = 1000;
const PRODUCT_IDS = [
    'iphone11'
];

function generateOrder() {
    return {
        orderLines: [
            {
                product: { id: PRODUCT_IDS[Math.floor(Math.random() * PRODUCT_IDS.length)] },
                quantity: faker.number.int({ min: 1, max: 5 }),
            },
        ],
        customer: {
            firstName: faker.person.firstName(),
            lastName: faker.person.lastName(),
            emailAddress: faker.internet.email(),
            phoneNumber: faker.number.int({ min: 1000000000, max: 9999999999 }).toString(),
        },
        orderDate: faker.date.recent().toISOString().slice(0, 16), // "YYYY-MM-DDTHH:mm"
    };
}

async function sendBatchOrders(count) {
    const orders = Array.from({ length: count }, generateOrder);

    try {
        const response = await axios.post(API_URL, orders, {
            headers: { 'Content-Type': 'application/json' },
        });
        console.log(`Sent ${count} orders:`, response.status, response.data);
    } catch (error) {
        console.error('Error sending orders:', error.status, error.response.data);
    }
}

// Generate and send between 1 and 1000 fake orders
const randomCount = faker.number.int({ min: 1, max: MAX_ORDERS });
sendBatchOrders(randomCount);