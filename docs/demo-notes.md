# Demo Notes

This version intentionally keeps the data layer simple:

- Orders are real rows in PostgreSQL, but they are demo rows.
- Policy documents are embedded into PGVector at startup.
- The AI layer is not mocked.
- Spring AI performs tool calling for order and compatibility questions.
- The same assistant service is used by blocking and streaming chat endpoints.

For a later production version, replace the OrderRepository tool implementation with an HTTP client to the retailer's live order API.
