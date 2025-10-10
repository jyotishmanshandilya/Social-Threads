CREATE TABLE IF NOT EXISTS address (
    id UUID DEFAULT gen_random_uuid() PRIMARY KEY,
    street TEXT,
    city TEXT,
    state TEXT,
    country TEXT,
    pincode TEXT,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE IF NOT EXISTS user_config (
    user_id UUID DEFAULT gen_random_uuid() PRIMARY KEY,
    address_id UUID,
    first_name TEXT,
    last_name TEXT,
    username TEXT NOT NULL UNIQUE,
    email TEXT,
    phone_number TEXT,
    encrypted_password TEXT,
    bio TEXT,
    profile_picture_url TEXT,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    account_type TEXT DEFAULT 'PRIVATE'
);

CREATE TABLE IF NOT EXISTS post (
    post_id UUID DEFAULT gen_random_uuid() PRIMARY KEY,
    user_id UUID NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    media_content_url TEXT,
    text_content TEXT,
    post_type TEXT NOT NULL
);

CREATE INDEX IF NOT EXISTS idx_post_user_id ON post(user_id);

CREATE TABLE IF NOT EXISTS comment (
   comment_id UUID DEFAULT gen_random_uuid() PRIMARY KEY,
    post_id UUID NOT NULL,
    user_id UUID NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE INDEX IF NOT EXISTS idx_comment_post_id ON comment(post_id);
CREATE INDEX IF NOT EXISTS idx_comment_user_id ON comment(user_id);

CREATE TABLE IF NOT EXISTS post_like (
    like_id UUID DEFAULT gen_random_uuid() PRIMARY KEY,
    post_id UUID NOT NULL,
    user_id UUID NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE UNIQUE INDEX IF NOT EXISTS uq_postlike_user_post ON post_like(user_id, post_id);

CREATE TABLE IF NOT EXISTS comment_like (
    comment_like_id UUID DEFAULT gen_random_uuid() PRIMARY KEY,
    comment_id UUID NOT NULL,
    post_id UUID NOT NULL,
    user_id UUID NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE UNIQUE INDEX IF NOT EXISTS uq_commentlike_user_comment ON comment_like(user_id, comment_id);





