alter table adicional
    add column if not exists preco numeric(10, 2);

update adicional
set preco = 0
where preco is null;

alter table adicional
    alter column preco set not null;
