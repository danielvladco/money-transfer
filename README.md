## Money transfer

### Requirements:
Java 11

### REST API

Endpoints:
- `POST /account` create an account.

    Body request example: 
    ```json 
    {"id":"some-id", "currency": "USD"}
    ```

    Body response example:
    ```json 
    {
        "data": {"id": "some-id", "currency": "USD"}, 
        "success": true
    }
    ```
- `GET /account` get all accounts

    Body response example:
    ```json 
    {
        "data": [
            {"id": "some-id1", "currency": "USD"},
            {"id": "some-id2", "currency": "USD"}
        ], 
        "success": true
    }
    ``` 
- `GET /account/{accountId}` get account
     
     Body response example:
     ```json 
     {
         "data": {"id": "some-id", "currency": "USD"}, 
         "success": true
     }
     ```
- `GET /account/{accountId}/balance` get account balance

     Body response example:
     ```json 
     {
         "data": 350, 
         "success": true
     }
     ``` 
- `GET /account/{accountId}/transactions` account transactions

     Body response example:
     ```json 
     {
         "data": [
            {"id": "transaction-id1", "accountId": "account-id1", "amount": 100, "currency": "USD"},
            {"id": "transaction-id2", "accountId": "account-id1", "amount": 130, "currency": "USD"}
         ],
         "success": true
     }
     ```
- `GET /transaction` get all transactions

     Body response example:
     ```json 
     {
         "data": [
            {"id": "transaction-id1", "accountId": "account-id1", "amount": 100, "currency": "USD"},
            {"id": "transaction-id2", "accountId": "account-id1", "amount": 130, "currency": "USD"}
         ], 
         "success": true
     }
     ```
- `GET /transaction/{transactionId}` get transaction

     Body response example:
     ```json 
     {
         "data": {"id": "transaction-id1", "accountId": "account-id1", "amount": 100, "currency": "USD"}, 
         "success": true
     }
     ```
- `POST /transfer` transfers money between accounts

    Body request example: 
    ```json 
    {
        "targetAccountId": "target-account-id1",
        "sourceAccountId": "source-account-id2",
        "amount": 200
    }
    ```

   Body response example:
    ```json 
    {
        "data": {"id":"some-id", "currency": "USD"}, 
        "success": true
    }
    ```
    
Error response:

```json
{
    "data": {
        "code": "ERROR_CODE",
        "message": "ERROR_MESSAGE"
    },
    "success": false
}
```

Status code `400`:

Error codes:
- `insufficient_funds`
- `mismatching_currencies`
- `transaction_invalid`
- `transaction_not_found`
- `account_invalid`
- `account_not_found`