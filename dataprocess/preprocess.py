import pandas as pd

SOURCE_PATH = "/temp/cs4224v/project_files/data_files"
DIRECTION_PATH = "/temp/cs4224v/processed_data/"

def main():
    # given table
    warehouse = pd.read_csv(f'{SOURCE_PATH}/warehouse.csv', header=None)
    district = pd.read_csv(f'{SOURCE_PATH}/district.csv', header=None)
    customer = pd.read_csv(f'{SOURCE_PATH}/customer.csv', header=None)
    item = pd.read_csv(f'{SOURCE_PATH}/item.csv', header=None)
    orders = pd.read_csv(f'{SOURCE_PATH}/order.csv', header=None)
    order_line = pd.read_csv(f'{SOURCE_PATH}/order-line.csv', header=None)
    stock = pd.read_csv(f'{SOURCE_PATH}/stock.csv', header=None)

    warehouse.columns = ['w_id', 'w_name', 'w_street_1', 'w_street_2', 'w_city', 'w_state', 'w_zip', 'w_tax', 'w_ytd']
    district.columns = ['d_w_id', 'd_id', 'd_name', 'd_street_1', 'd_street_2', 'd_city', 'd_state', 'd_zip', 'd_tax', 'd_ytd', 'd_next_o_id']
    customer.columns = ['c_w_id', 'c_d_id', 'c_id', 'c_first', 'c_middle', 'c_last', 'c_street_1', 'c_street_2', 'c_city', 'c_state', 'c_zip', 'c_phone', 'c_since', 'c_credit', 'c_credit_lim', 'c_discount', 'c_balance', 'c_ytd_payment', 'c_payment_cnt', 'c_delivery_cnt', 'c_data']
    orders.columns = ['o_w_id', 'o_d_id', 'o_id', 'o_c_id', 'o_carrier_id', 'o_ol_cnt', 'o_all_local', 'o_entry_d']
    item.columns = ['i_id', 'i_name', 'i_price', 'i_im_id', 'i_data']
    order_line.columns = ['ol_w_id', 'ol_d_id', 'ol_o_id', 'ol_number', 'ol_i_id', 'ol_delivery_d', 'ol_amount', 'ol_supply_w_id','ol_quantity', 'ol_dist_info']
    stock.columns = ['s_w_id', 's_i_id', 's_quantity', 's_ytd', 's_order_cnt', 's_remote_cnt', 's_dist_01', 's_dist_02', 's_dist_03', 's_dist_04', 's_dist_05', 's_dist_06', 's_dist_07', 's_dist_08', 's_dist_09', 's_dist_10', 's_data']

    customer['c_state'] = customer['c_state'].fillna('unknown')
    orders['o_carrier_id'] = orders['o_carrier_id'].fillna(-1)
    orders = orders.astype({"o_carrier_id": int})
    order_line['ol_delivery_d'] = order_line['ol_delivery_d'].fillna(pd.Timestamp(0))
    stock = stock.astype({"s_ytd": int})

    # customer_order_items
    customer_order_items = order_line[['ol_w_id', 'ol_d_id', 'ol_o_id', 'ol_i_id']]
    customer_order_items.columns = ['o_w_id', 'o_d_id', 'o_id', 'ol_i_id']
    customer_order_items = pd.merge(customer_order_items, orders, on=['o_w_id', 'o_d_id', 'o_id'], how='left')
    customer_order_items = customer_order_items[['o_w_id', 'o_d_id', 'o_id', 'ol_i_id', 'o_c_id']]
    customer_order_items.columns = ['coi_w_id', 'coi_d_id', 'coi_o_id', 'coi_i_id', 'coi_c_id']

    # customer_balance
    customer_balance = customer[['c_id', 'c_w_id', 'c_d_id', 'c_balance', 'c_first', 'c_middle', 'c_last']]

    warehouse.to_csv(f'{DIRECTION_PATH}/warehouse.csv', index=False)
    district.to_csv(f'{DIRECTION_PATH}/district.csv', index=False)
    customer.to_csv(f'{DIRECTION_PATH}/customer.csv', index=False)
    item.to_csv(f'{DIRECTION_PATH}/item.csv', index=False)
    orders.to_csv(f'{DIRECTION_PATH}/order.csv', index=False)
    order_line.to_csv(f'{DIRECTION_PATH}/order-line.csv', index=False)
    stock.to_csv(f'{DIRECTION_PATH}/stock.csv', index=False)
    customer_order_items.to_csv(f'{DIRECTION_PATH}/customer_order_items.csv', index=False)
    customer_balance.to_csv(f'{DIRECTION_PATH}/customer_balance.csv', index=False)

if __name__ == "__main__":
    main()