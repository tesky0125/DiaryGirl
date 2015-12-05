package yan.girl.interfaces;

public interface DialogListener {

	/**
	 * 通过该接口函数去接收点击ListView的item的position值，当然还可以作为其他参数的传递者
	 * @param pos
	 */
	void onItemClick(int pos);
	
}
