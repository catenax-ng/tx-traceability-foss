## Query for assets with childs:

SELECT asset.id, asset.customer_part_id,asset.id_short, asset.manufacturer_id, asset.manufacturer_name, asset.manufacturer_part_id, asset.manufacturing_country, asset.manufacturing_date, asset.name_at_customer, asset.name_at_manufacturer, asset.quality_type, asset.batch_id, asset.part_instance_id, asset.van
FROM public.asset
         JOIN asset_child_descriptors
              ON asset.id = asset_child_descriptors.id;

## Query for assets with parents:

SELECT asset.id, asset.customer_part_id,asset.id_short, asset.manufacturer_id, asset.manufacturer_name, asset.manufacturer_part_id, asset.manufacturing_country, asset.manufacturing_date, asset.name_at_customer, asset.name_at_manufacturer, asset.quality_type, asset.batch_id, asset.part_instance_id, asset.van
FROM public.asset
         JOIN asset_parent_descriptors
              ON asset.id = asset_parent_descriptors.id;


