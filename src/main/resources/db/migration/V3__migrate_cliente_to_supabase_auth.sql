alter table cliente
    add column if not exists auth_user_id uuid;

alter table cliente
    drop column if exists senha;

create unique index if not exists uk_cliente_auth_user_id
    on cliente (auth_user_id);
