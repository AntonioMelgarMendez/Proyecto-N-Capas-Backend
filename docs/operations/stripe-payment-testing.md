---
title: Stripe Payment Manual Testing
author: Proyecto N-Capas Team
version: v1.0.0
last_updated: 2026-06-11
---

## Prerequisites

- Stripe test mode keys configured in environment variables
- Backend running on default port
- Stripe CLI forwarding webhooks: `stripe listen --forward-to localhost:8080/api/webhooks/stripe`

## Test 1 — Reservation checkout cancel

1. Create a booking: `POST /api/reservations/{propertyId}/book`
2. Start checkout: `POST /api/payments/checkout/{reservationId}`
3. Cancel in Stripe UI or call: `POST /api/payments/cancel/{reservationId}`
4. Verify reservation status is `EXPIRED` and calendar dates are released

## Test 2 — Reservation payment success

1. Create booking and start checkout
2. Pay with Stripe test card `4242 4242 4242 4242`
3. Confirm via webhook or `POST /api/payments/confirm-session/{sessionId}`
4. Verify reservation status is `CONFIRMED`

## Test 3 — Extension with approval and payment

1. On a confirmed reservation: `POST /api/reservations/{id}/extend/request?extraDays=2`
2. Approve as landlord: `POST /api/reservations/extend/{requestId}/approve?landlordId={id}`
3. Pay extension: `POST /api/payments/checkout/extension/{requestId}`
4. Complete Stripe checkout
5. Verify checkout date extended and extension request status is `PAID`

## Test 4 — Cancellation with partial refund

1. On a confirmed paid reservation: `POST /api/reservations/{id}/cancel/quote`
2. Confirm: `POST /api/reservations/{id}/cancel/confirm`
3. Verify Stripe Dashboard shows partial refund when penalty applies

## Test 5 — Deposit refund (optional amount)

1. `POST /api/payments/refund/{reservationId}?amount=50.00`
2. Verify payment status becomes `PARTIALLY_REFUNDED` or `REFUNDED`

## Test 6 — Extension checkout without approval

1. Call `POST /api/payments/checkout/extension/{requestId}` on a `PENDING` request
2. Expect HTTP 409 with invalid payment state message
