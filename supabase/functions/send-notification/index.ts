import { serve } from "https://deno.land/std@0.177.0/http/server.ts";
import { createClient } from "https://esm.sh/@supabase/supabase-js@2";

const FIREBASE_SERVER_KEY = Deno.env.get("FIREBASE_SERVER_KEY")!;

serve(async (req) => {
  const { type, record } = await req.json();

  const supabase = createClient(
    Deno.env.get("SUPABASE_URL")!,
    Deno.env.get("SUPABASE_SERVICE_ROLE_KEY")!,
  );

  let targetUserId: string | null = null;
  let title = "Kaam";
  let body = "";

  switch (type) {
    case "new_booking":
      targetUserId = record.worker_id;
      title = "New Booking Request";
      body = `You have a new ${record.service} booking request`;
      break;
    case "booking_accepted":
      targetUserId = record.customer_id;
      title = "Booking Accepted";
      body = "Your booking has been accepted!";
      break;
    case "new_message": {
      const { data: conv } = await supabase
        .from("conversations")
        .select("customer_id, worker_id")
        .eq("id", record.conversation_id)
        .single();
      if (conv) {
        targetUserId = record.sender_id === conv.customer_id
          ? conv.worker_id
          : conv.customer_id;
      }
      title = "New Message";
      body = record.content?.substring(0, 100) ?? "";
      break;
    }
    case "new_application": {
      const { data: job } = await supabase
        .from("job_requests")
        .select("customer_id")
        .eq("id", record.job_request_id)
        .single();
      if (job) {
        targetUserId = job.customer_id;
      }
      title = "New Application";
      body = "A worker has applied to your job request";
      break;
    }
  }

  if (!targetUserId) return new Response("No target", { status: 200 });

  // Get FCM token
  const { data: profile } = await supabase
    .from("profiles")
    .select("fcm_token")
    .eq("id", targetUserId)
    .single();

  if (!profile?.fcm_token) return new Response("No token", { status: 200 });

  // Send via FCM
  await fetch("https://fcm.googleapis.com/fcm/send", {
    method: "POST",
    headers: {
      "Content-Type": "application/json",
      "Authorization": `key=${FIREBASE_SERVER_KEY}`,
    },
    body: JSON.stringify({
      to: profile.fcm_token,
      notification: { title, body },
    }),
  });

  return new Response("OK", { status: 200 });
});
