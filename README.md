# stitch-sample-integration

Repository that integrates to stitch.money api using Java(Spring)

### Integrations includes:
- LinkPay
- InstantPay
- Refund
- Common data queries
- Webhooks

### Application Requirements:
- Jdk 15 enabled environment
- This application uses redis-cache as its in memory database. Run the following command to set it up
- Use ng-rok to generate a host name that would be attached as the base url
- Follow documentation and create you client id, then go to the retool app update using the below snippet

#### Run redis dependency
```
docker run -d --name redis-stack-server -p 6379:6379 redis/redis-stack-server:latest
```

#### Update client details in retool
```
{
    "clientId": "test-e0be6afd-d1c2-4e39-a912-06c2ade4c768",
    "redirectUris": [
      "https://82a7-105-112-18-170.eu.ngrok.io/keanhive/api/data/retrieve-token",
      "https://82a7-105-112-18-170.eu.ngrok.io/keanhive/api/instant-pay/conclude",
      "https://82a7-105-112-18-170.eu.ngrok.io/keanhive/api/web-hook",
      "https://82a7-105-112-18-170.eu.ngrok.io/keanhive/api/link-pay",
      "https://82a7-105-112-18-170.eu.ngrok.io/keanhive/api/link-pay/retrieve-token",
      "https://82a7-105-112-18-170.eu.ngrok.io/keanhive/api/link-pay/multifactor",
      "https://82a7-105-112-18-170.eu.ngrok.io/keanhive/api/link-pay/get-subscription"
    ],
    "floatAccounts": [
    {
      "AccountNumber":"987654321",
      "BankId":"nedbank",
      "BeneficiaryName":"FizzBuzz Co."
    }
  ],
  "settlementAccounts":  [
    {
      "AccountNumber":"123456789",
      "BankId":"nedbank",
      "BeneficiaryName":"FizzBuzz Co."
    }
  ],
    "alphaFlags": [
    "refund",
    "categorization",
    "bankstatements",
    "incomeestimation",
    "paymentauthorizationrequest"
  ]
  }
  
```

Postman collection: https://www.getpostman.com/collections/eafb2467a3b7fb437ab1

For common errors encountered, checkout:

https://support.stitch.money/hc/en-us/articles/6243415533073-I-m-getting-an-unauthorized-client-error-