# Kaam — App Design Spec

**Date:** 2026-03-31
**Platform:** Android (Kotlin + Jetpack Compose), iOS planned for later
**Target Market:** Nepal (Kathmandu first, expandable to other cities)
**Purpose:** Marketplace connecting busy urban professionals with verified home service workers (cleaning, babysitting/nanny)

---

## 1. Overview

Kaam ("work" in Nepali/Hindi) is a mobile platform for hiring verified maids, nannies, and helpers. Customers browse workers or post job requests. Workers onboard, get verified by the admin team, and receive bookings. The app prioritizes trust (verification), minimal friction (few fields, fast booking), and a clean modern UI.

This is an experimental project to learn the end-to-end flow of building and publishing an Android app on the Play Store.

---

## 2. Architecture

### Tech Stack
- **App:** Kotlin + Jetpack Compose, MVVM architecture, Material 3, Navigation Compose, Hilt (DI)
- **Backend:** Supabase (Auth, PostgreSQL, Storage, Realtime, Edge Functions)
- **Push Notifications:** Firebase Cloud Messaging (FCM)
- **OTP:** Nepal SMS provider (Sparrow SMS or Aakash SMS)
- **Distribution:** Google Play Store

### Scalability
- Supabase free tier: ~50K MAU, 500MB DB, 1GB storage (sufficient for Kathmandu launch)
- Supabase Pro ($25/mo): 100K+ MAU, 8GB DB, 100GB storage
- Migration path: self-hosted Supabase or raw PostgreSQL on any cloud when needed
- All components are independently replaceable

---

## 3. Service Categories (MVP)

1. **House Cleaning**
2. **Babysitting / Nanny**

More categories (pet care, elderly care, cooking, etc.) can be added in future updates.

---

## 4. User Roles

### Customer
- Hires workers for cleaning or babysitting
- No document verification required

### Worker
- Provides cleaning or nanny services
- Must be verified by admin team before becoming bookable

### Admin (future web panel)
- Verifies worker identity documents
- Manages pricing ranges, users, and reports

---

## 5. Onboarding

### Customer Registration
**Required:** Full name, Phone number (+977 OTP verified), City/Area
**Optional:** Profile photo, Address (can provide during booking)

### Worker Registration
**Required:** Full name, Phone number (+977 OTP verified), Profile photo, Service type(s), Preferred city/area, Citizenship document photo, Hourly/daily rate (within platform range)
**Optional:** Short bio/experience, Payment QR code image (eSewa/Khalti), Payment mobile number

---

## 6. Booking Flow

### Direct Booking (Path A)
1. Customer browses workers (filter by service, area, rate)
2. Customer views worker profile (photo, bio, services, rate, reviews, verified badge)
3. Customer taps "Book" — selects date, time, address, adds notes
4. Worker receives push notification → accepts or declines
5. Chat unlocks for both parties to discuss details
6. After service, customer pays via worker's QR code / mobile number
7. Customer leaves a review (1-5 stars + comment)

### Job Request (Path B)
1. Customer posts a request (service type, description, date, budget range, area)
2. Nearby verified workers see the request on their Job Board
3. Workers apply with proposed rate and message
4. Customer reviews applicants and picks one
5. Chat unlocks → continues as direct booking from step 5

---

## 7. Pricing

- Workers set their own rates within platform-defined min/max ranges
- Ranges are per service type, per city (managed by admin)
- No in-app payment processing — workers upload their eSewa/Khalti QR code or mobile number
- Customers pay workers directly (peer-to-peer) via QR scan or mobile transfer
- App shows worker's payment info after booking is confirmed
- ToS must clearly state Kaam is not a payment intermediary

**Compliance note:** Displaying worker payment info (QR/mobile number) does not require NRB payment licensing. This is a common pattern in Nepal's marketplace apps. No transaction visibility means no dispute resolution on payments — acceptable for MVP.

---

## 8. Worker Verification

Multi-step process managed by admin team:

1. **Document Upload** (in-app) — citizenship document photo
2. **Phone Call Verification** (optional) — admin calls worker to confirm identity
3. **Physical Office Visit** (optional) — in-person verification if needed

- Worker profile shows "Pending Verification" until approved (not bookable)
- Admin can mark each step as completed
- Worker becomes "Verified" when admin is satisfied
- If rejected, worker receives notification with reason and can re-upload documents

---

## 9. Chat System

- In-app messaging between customer and worker
- **Unlocks after:** a booking request is sent OR a job inquiry is initiated (not open to all)
- Tied to a specific booking or job request (context bar shown in chat)
- Supports text messages with read receipts
- Push notification for new messages
- Keeps communication on-platform (no need to share personal phone numbers for coordination)

---

## 10. Notifications (Push)

Key events triggering push notifications:
- New booking request (to worker)
- Booking accepted/declined (to customer)
- New chat message
- New job request matching worker's service/area
- Job application received (to customer)
- Verification status update (to worker)
- Booking reminder (day before)
- Review received (to worker)

---

## 11. Data Model

### profiles
| Column | Type | Notes |
|--------|------|-------|
| id | uuid PK | Links to Supabase Auth |
| full_name | text | |
| phone | text | Unique, +977 format |
| avatar_url | text | |
| role | enum | customer, worker |
| city | text | |
| area | text | |
| created_at | timestamp | |

### worker_profiles
| Column | Type | Notes |
|--------|------|-------|
| id | uuid PK/FK | → profiles |
| bio | text | Optional |
| services | enum[] | [cleaning, nanny] |
| hourly_rate | int | NPR |
| citizenship_doc_url | text | Storage path |
| payment_qr_url | text | Optional |
| payment_phone | text | Optional |
| verification_status | enum | pending, verified, rejected |
| rejection_reason | text | Nullable |
| is_available | boolean | Toggle |

### bookings
| Column | Type | Notes |
|--------|------|-------|
| id | uuid PK | |
| customer_id | uuid FK | → profiles |
| worker_id | uuid FK | → profiles, nullable for requests |
| type | enum | direct, request |
| service | enum | cleaning, nanny |
| date | date | |
| time_slot | text | |
| address | text | |
| notes | text | |
| status | enum | pending, accepted, declined, completed, cancelled |
| created_at | timestamp | |

### job_requests
| Column | Type | Notes |
|--------|------|-------|
| id | uuid PK | |
| customer_id | uuid FK | → profiles |
| service | enum | cleaning, nanny |
| description | text | |
| date | date | |
| budget_max | int | NPR |
| city | text | |
| area | text | |
| status | enum | open, filled, closed |
| created_at | timestamp | |

### job_applications
| Column | Type | Notes |
|--------|------|-------|
| id | uuid PK | |
| job_request_id | uuid FK | → job_requests |
| worker_id | uuid FK | → profiles |
| proposed_rate | int | NPR |
| message | text | |
| status | enum | pending, accepted, rejected |
| created_at | timestamp | |

### conversations
| Column | Type | Notes |
|--------|------|-------|
| id | uuid PK | |
| booking_id | uuid FK | Nullable |
| job_request_id | uuid FK | Nullable |
| customer_id | uuid FK | → profiles |
| worker_id | uuid FK | → profiles |
| created_at | timestamp | |

### messages
| Column | Type | Notes |
|--------|------|-------|
| id | uuid PK | |
| conversation_id | uuid FK | → conversations |
| sender_id | uuid FK | → profiles |
| content | text | |
| sent_at | timestamp | |
| read_at | timestamp | Nullable |

### reviews
| Column | Type | Notes |
|--------|------|-------|
| id | uuid PK | |
| booking_id | uuid FK | → bookings |
| reviewer_id | uuid FK | → profiles (customer) |
| worker_id | uuid FK | → profiles (worker) |
| rating | int | 1-5 |
| comment | text | |
| created_at | timestamp | |

---

## 12. App Screens (~23 total)

### Auth & Onboarding (5)
1. Welcome — logo, tagline, "Get Started"
2. Phone Entry — +977 input, send OTP
3. OTP Verify — 6-digit code, auto-read SMS
4. Role Select — "I need help" (customer) / "I want to work" (worker)
5. Basic Info — name, city/area, photo (required for workers)

### Customer Screens (8)
1. Home — service cards (Cleaning / Nanny), active bookings summary
2. Worker List — filter by service, area, rate; worker cards with photo, rating, rate
3. Worker Profile — photo, bio, services, rate, reviews, verified badge, Book/Inquire buttons
4. Booking Form — date picker, time slot, address, notes, confirm
5. Post Job Request — service type, description, date, budget range, area
6. My Bookings — active/past tabs, status indicators, quick actions
7. Job Applicants — workers who applied, their rate & message, accept/reject
8. Payment Screen — worker's QR code display, mobile number, "I've paid" confirm

### Worker Screens (6)
1. Profile Setup — services picker, rate slider, bio, ID upload, payment QR upload
2. Verification Status — pending/approved/rejected with reason, re-upload option
3. Dashboard — incoming bookings, availability toggle, earnings summary
4. Job Board — open requests nearby, filter by service, apply with rate
5. Booking Detail — customer info, date, address, notes, accept/decline, mark complete
6. My Reviews — rating overview, customer feedback, average score

### Shared Screens (4)
1. Chat List — all conversations, last message preview, unread badge
2. Chat Room — messages, booking context bar, text input
3. Profile/Settings — edit profile, notification prefs, help/support, logout
4. Notifications — all notifications, booking updates, chat alerts, verification status

### Bottom Navigation
- **Customer:** Home | Bookings | Chat | Profile
- **Worker:** Dashboard | Jobs | Chat | Profile

---

## 13. Visual Identity

### Design Style: Soft UI Evolution
Clean depth without heaviness. Subtle shadows create hierarchy. High contrast for trust and readability. Premium but approachable.

### Color Palette
| Role | Color | Hex |
|------|-------|-----|
| Primary | Cyan 600 | #0891B2 |
| Secondary | Cyan 400 | #22D3EE |
| CTA / Success | Green 500 | #22C55E |
| Error / Decline | Red 500 | #EF4444 |
| Warning / Pending | Amber 500 | #F59E0B |
| Background | White | #FFFFFF |
| Surface | Cyan tint | #F8FFFE |
| Surface Tint | Cyan 50 | #ECFEFF |
| Text Primary | Cyan 900 | #164E63 |
| Text Secondary | Slate | #5F7D8A |

### Typography
**Font:** DM Sans (Google Fonts) — geometric, modern, premium
- Headings: Bold (700) / SemiBold (600)
- Body: Regular (400), 16sp minimum
- Captions: Regular (400), 14sp

### Component Guidelines
- **Buttons:** 12dp corner radius, 48dp minimum height (touch target), filled primary / outlined secondary / green CTA
- **Cards:** 16dp corner radius, soft elevation shadow, no hard borders
- **Status chips:** Pill shape (20dp radius), tinted background with matching text color
- **Spacing:** 8dp base unit, 16dp screen padding, 12dp card gap, 24dp section gap
- **Touch targets:** 48dp minimum, 8dp minimum gap between targets
- **Transitions:** 200-300ms for micro-interactions
- **Icons:** Material Icons or Lucide (no emojis as UI icons)

### Design Principles
1. **Trust First** — verified badges, real photos, visible reviews
2. **Minimal Friction** — 3 taps to book, no unnecessary fields, progressive disclosure
3. **Soft Depth** — subtle shadows, rounded corners, tinted surfaces
4. **Clear Status** — color-coded chips, progress indicators, users always know what's happening

---

## 14. MVP Scope

### In Scope
- Customer and worker registration with phone OTP
- Worker profile with document upload and payment QR
- Direct booking flow
- Job request + application flow
- In-app chat (unlocked after booking/inquiry)
- Worker verification workflow (admin marks as verified)
- Push notifications for key events
- Reviews and ratings
- Play Store release

### Out of Scope (Future)
- Admin web panel (initially verify via direct Supabase dashboard)
- In-app payment processing
- Additional service categories
- iOS app
- Multi-language support
- Advanced search/filters (location radius, availability calendar)
- Background checks integration
- Analytics dashboard

---

## 15. Admin Verification (MVP Workaround)

Until the admin web panel is built, verification can be done through:
- Supabase Dashboard — directly update `worker_profiles.verification_status`
- A simple Supabase Edge Function triggered by admin (optional)

This keeps MVP scope tight while still enabling the verification flow.
