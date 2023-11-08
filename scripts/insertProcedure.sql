-- Create a stored procedure
CREATE OR REPLACE PROCEDURE insert_new_user(
    IN login VARCHAR(255),
	IN passwd VARCHAR(255),
	IN fullName VARCHAR(255),
	IN street VARCHAR(255),
	IN zip INT,
	IN city VARCHAR(255),
	IN phone VARCHAR(12)
)
AS $$
DECLARE
    partner_id INT;
	user_id INT;
BEGIN
    -- Insert data into the res_partner table
    INSERT INTO public.res_partner (name, type, street, zip, city, email, phone, active, is_company, partner_share)
	VALUES (fullName, 'contact', street, zip, city, login, phone, true, false, true) RETURNING id INTO partner_id;
    
    -- Insert data into the res_users table using the obtained partner_id
    INSERT INTO public.res_users (company_id, partner_id, login, password)
	VALUES (2, partner_id, login, passwd) RETURNING id INTO user_id;
	
	--Insert data into public.res_company_users_rel using the obtained user_id
	INSERT INTO public.res_company_users_rel (cid, user_id)
	VALUES (2, user_id);
    
EXCEPTION
    -- Handle exceptions by rolling back the transaction
    WHEN OTHERS THEN
        -- Rollback the transaction in case of an exception
        ROLLBACK;
        RAISE EXCEPTION 'An error occurred: %', SQLERRM;
END;
$$ LANGUAGE plpgsql

