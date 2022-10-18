# stitch-sample-integration

Repository that integrates to stitch.money api using Java(Spring)

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