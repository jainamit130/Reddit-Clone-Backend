CREATE OR REPLACE FUNCTION strip_html_tags(html TEXT) RETURNS TEXT AS $$
BEGIN
    RETURN regexp_replace(html, '<[^>]*>', '', 'g');
END;
$$ LANGUAGE plpgsql;