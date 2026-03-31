-- Enable RLS on all tables
ALTER TABLE profiles ENABLE ROW LEVEL SECURITY;
ALTER TABLE worker_profiles ENABLE ROW LEVEL SECURITY;
ALTER TABLE bookings ENABLE ROW LEVEL SECURITY;
ALTER TABLE job_requests ENABLE ROW LEVEL SECURITY;
ALTER TABLE job_applications ENABLE ROW LEVEL SECURITY;
ALTER TABLE conversations ENABLE ROW LEVEL SECURITY;
ALTER TABLE messages ENABLE ROW LEVEL SECURITY;
ALTER TABLE reviews ENABLE ROW LEVEL SECURITY;

-- PROFILES
CREATE POLICY "Anyone can view profiles" ON profiles FOR SELECT USING (true);
CREATE POLICY "Users can update own profile" ON profiles FOR UPDATE USING (auth.uid() = id);
CREATE POLICY "Users can insert own profile" ON profiles FOR INSERT WITH CHECK (auth.uid() = id);

-- WORKER PROFILES
CREATE POLICY "Anyone can view worker profiles" ON worker_profiles FOR SELECT USING (true);
CREATE POLICY "Workers can update own worker profile" ON worker_profiles FOR UPDATE USING (auth.uid() = id);
CREATE POLICY "Workers can insert own worker profile" ON worker_profiles FOR INSERT WITH CHECK (auth.uid() = id);

-- BOOKINGS
CREATE POLICY "Users can view own bookings" ON bookings FOR SELECT USING (auth.uid() = customer_id OR auth.uid() = worker_id);
CREATE POLICY "Customers can create bookings" ON bookings FOR INSERT WITH CHECK (auth.uid() = customer_id);
CREATE POLICY "Involved parties can update bookings" ON bookings FOR UPDATE USING (auth.uid() = customer_id OR auth.uid() = worker_id);

-- JOB REQUESTS
CREATE POLICY "Anyone can view open job requests" ON job_requests FOR SELECT USING (status = 'open' OR auth.uid() = customer_id);
CREATE POLICY "Customers can create job requests" ON job_requests FOR INSERT WITH CHECK (auth.uid() = customer_id);
CREATE POLICY "Customers can update own job requests" ON job_requests FOR UPDATE USING (auth.uid() = customer_id);

-- JOB APPLICATIONS
CREATE POLICY "Involved parties can view applications" ON job_applications FOR SELECT USING (auth.uid() = worker_id OR auth.uid() IN (SELECT customer_id FROM job_requests WHERE id = job_request_id));
CREATE POLICY "Workers can create applications" ON job_applications FOR INSERT WITH CHECK (auth.uid() = worker_id);
CREATE POLICY "Involved parties can update applications" ON job_applications FOR UPDATE USING (auth.uid() = worker_id OR auth.uid() IN (SELECT customer_id FROM job_requests WHERE id = job_request_id));

-- CONVERSATIONS
CREATE POLICY "Users can view own conversations" ON conversations FOR SELECT USING (auth.uid() = customer_id OR auth.uid() = worker_id);
CREATE POLICY "Users can create conversations they're part of" ON conversations FOR INSERT WITH CHECK (auth.uid() = customer_id OR auth.uid() = worker_id);

-- MESSAGES
CREATE POLICY "Conversation participants can view messages" ON messages FOR SELECT USING (conversation_id IN (SELECT id FROM conversations WHERE customer_id = auth.uid() OR worker_id = auth.uid()));
CREATE POLICY "Conversation participants can send messages" ON messages FOR INSERT WITH CHECK (auth.uid() = sender_id AND conversation_id IN (SELECT id FROM conversations WHERE customer_id = auth.uid() OR worker_id = auth.uid()));
CREATE POLICY "Recipients can mark messages as read" ON messages FOR UPDATE USING (auth.uid() != sender_id AND conversation_id IN (SELECT id FROM conversations WHERE customer_id = auth.uid() OR worker_id = auth.uid()));

-- REVIEWS
CREATE POLICY "Anyone can view reviews" ON reviews FOR SELECT USING (true);
CREATE POLICY "Customers can create reviews for their completed bookings" ON reviews FOR INSERT WITH CHECK (auth.uid() = reviewer_id AND booking_id IN (SELECT id FROM bookings WHERE customer_id = auth.uid() AND status = 'completed'));
