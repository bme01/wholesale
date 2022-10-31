\connect cs5424_ysql;
\copy wholesale.warehouse from '/temp/cs4224v/project_files/data_files/warehouse.csv' csv;
\copy wholesale.district from '/temp/cs4224v/project_files/data_files/district.csv' csv;
\copy wholesale.customer from '/temp/cs4224v/project_files/data_files/customer.csv' csv;
\copy wholesale."order" from '/temp/cs4224v/project_files/data_files/order.csv' with NULL AS 'null' csv;
\copy wholesale.item from '/temp/cs4224v/project_files/data_files/item.csv' csv;
\copy wholesale.order_line from '/temp/cs4224v/project_files/data_files/order-line.csv' with NULL AS 'null' csv;
\copy wholesale.stock from '/temp/cs4224v/project_files/data_files/stock.csv' csv;