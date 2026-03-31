-- Enable UUID extension
CREATE EXTENSION IF NOT EXISTS "uuid-ossp";

-- Enum types
CREATE TYPE user_role AS ENUM ('customer', 'worker');
CREATE TYPE service_type AS ENUM ('cleaning', 'nanny');
CREATE TYPE verification_status AS ENUM ('pending', 'verified', 'rejected');
CREATE TYPE booking_status AS ENUM ('pending', 'accepted', 'declined', 'completed', 'cancelled');
CREATE TYPE booking_type AS ENUM ('direct', 'request');
CREATE TYPE job_request_status AS ENUM ('open', 'filled', 'closed');
CREATE TYPE job_application_status AS ENUM ('pending', 'accepted', 'rejected');

-- Profiles (extends Supabase auth.users)
CREATE TABLE profiles (
    id UUID PRIMARY KEY REFERENCES auth.users(id) ON DELETE CASCADE,
    full_name TEXT NOT NULL,
    phone TEXT UNIQUE NOT NULL,
    avatar_url TEXT,
    role user_role NOT NULL,
    city TEXT NOT NULL,
    area TEXT,
    created_at TIMESTAMPTZ DEFAULT NOW() NOT NULL
);

-- Worker profiles (1:1 with profiles where role = 'worker')
CREATE TABLE worker_profiles (
    id UUID PRIMARY KEY REFERENCES profiles(id) ON DELETE CASCADE,
    bio TEXT,
    services service_type[] NOT NULL DEFAULT '{}',
    hourly_rate INTEGER NOT NULL,
    citizenship_doc_url TEXT,
    payment_qr_url TEXT,
    payment_phone TEXT,
    verification_status verification_status DEFAULT 'pending' NOT NULL,
    rejection_reason TEXT,
    is_available BOOLEAN DEFAULT TRUE NOT NULL
);

-- Bookings
CREATE TABLE bookings (
    id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    customer_id UUID NOT NULL REFERENCES profiles(id),
    worker_id UUID REFERENCES profiles(id),
    type booking_type NOT NULL,
    service service_type NOT NULL,
    date DATE NOT NULL,
    time_slot TEXT NOT NULL,
    address TEXT NOT NULL,
    notes TEXT,
    status booking_status DEFAULT 'pending' NOT NULL,
    created_at TIMESTAMPTZ DEFAULT NOW() NOT NULL
);

-- Job requests
CREATE TABLE job_requests (
    id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    customer_id UUID NOT NULL REFERENCES profiles(id),
    service service_type NOT NULL,
    description TEXT NOT NULL,
    date DATE NOT NULL,
    budget_max INTEGER,
    city TEXT NOT NULL,
    area TEXT,
    status job_request_status DEFAULT 'open' NOT NULL,
    created_at TIMESTAMPTZ DEFAULT NOW() NOT NULL
);

-- Job applications
CREATE TABLE job_applications (
    id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    job_request_id UUID NOT NULL REFERENCES job_requests(id) ON DELETE CASCADE,
    worker_id UUID NOT NULL REFERENCES profiles(id),
    proposed_rate INTEGER NOT NULL,
    message TEXT,
    status job_application_status DEFAULT 'pending' NOT NULL,
    created_at TIMESTAMPTZ DEFAULT NOW() NOT NULL,
    UNIQUE(job_request_id, worker_id)
);

-- Conversations
CREATE TABLE conversations (
    id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    booking_id UUID REFERENCES bookings(id),
    job_request_id UUID REFERENCES job_requests(id),
    customer_id UUID NOT NULL REFERENCES profiles(id),
    worker_id UUID NOT NULL REFERENCES profiles(id),
    created_at TIMESTAMPTZ DEFAULT NOW() NOT NULL,
    UNIQUE(customer_id, worker_id, booking_id),
    UNIQUE(customer_id, worker_id, job_request_id)
);

-- Messages
CREATE TABLE messages (
    id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    conversation_id UUID NOT NULL REFERENCES conversations(id) ON DELETE CASCADE,
    sender_id UUID NOT NULL REFERENCES profiles(id),
    content TEXT NOT NULL,
    sent_at TIMESTAMPTZ DEFAULT NOW() NOT NULL,
    read_at TIMESTAMPTZ
);

-- Reviews
CREATE TABLE reviews (
    id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    booking_id UUID NOT NULL REFERENCES bookings(id),
    reviewer_id UUID NOT NULL REFERENCES profiles(id),
    worker_id UUID NOT NULL REFERENCES profiles(id),
    rating INTEGER NOT NULL CHECK (rating >= 1 AND rating <= 5),
    comment TEXT,
    created_at TIMESTAMPTZ DEFAULT NOW() NOT NULL,
    UNIQUE(booking_id, reviewer_id)
);

-- Indexes
CREATE INDEX idx_profiles_role ON profiles(role);
CREATE INDEX idx_profiles_city ON profiles(city);
CREATE INDEX idx_worker_profiles_verification ON worker_profiles(verification_status);
CREATE INDEX idx_worker_profiles_services ON worker_profiles USING GIN(services);
CREATE INDEX idx_bookings_customer ON bookings(customer_id);
CREATE INDEX idx_bookings_worker ON bookings(worker_id);
CREATE INDEX idx_bookings_status ON bookings(status);
CREATE INDEX idx_job_requests_status ON job_requests(status);
CREATE INDEX idx_job_requests_city ON job_requests(city);
CREATE INDEX idx_job_applications_job ON job_applications(job_request_id);
CREATE INDEX idx_conversations_customer ON conversations(customer_id);
CREATE INDEX idx_conversations_worker ON conversations(worker_id);
CREATE INDEX idx_messages_conversation ON messages(conversation_id);
CREATE INDEX idx_messages_sent_at ON messages(sent_at);
CREATE INDEX idx_reviews_worker ON reviews(worker_id);
