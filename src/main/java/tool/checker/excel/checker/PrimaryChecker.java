package tool.checker.excel.checker;

import java.util.Set;

import com.google.common.base.Preconditions;
import com.google.common.base.Strings;
import com.google.common.collect.Sets;

import tool.checker.excel.ExcelItem;
import tool.checker.excel.ExcelsData;
import tool.checker.excel.error.ErrorCatcher;

public final class PrimaryChecker extends BaseContentChecker {
	
	private boolean hasPrimary = false;
	
	private final Set<String> primaryKeys = Sets.newHashSet();

	@Override
	public boolean check(String content, ExcelItem item, ExcelsData excelsData) {
		// 检测主键列唯一性
		ErrorCatcher errorCatcher = excelsData.getErrorCatcher();
		if ("primary".equalsIgnoreCase(item.getIndex())) {
			if (hasPrimary) {
				errorCatcher.catchError(excel.getExcelName(), row, item.getName(), "重复的主键列 [" + item.getName() + "].");
				return false;
			} else {
				hasPrimary = true;
			}
			readPrimary(row, content, primaryKeys, item.getName(), errorCatcher);
		}
		return true;
	}
	
	private void readPrimary(int row, String content, Set<String> primaryKeys, String column, ErrorCatcher errorCatcher) {
		try {
			Preconditions.checkArgument(primaryKeys.add(content), "主键 [%s] 重复.", content);
		} catch (IllegalArgumentException e) {
			String error = e.getMessage();
			errorCatcher.catchError(excel.getExcelName(), row, column, (Strings.isNullOrEmpty(error) ? content + " 不能转为int." : error));
		}
	}

	@Override
	public void rowFinish() {}

	@Override
	protected void excelBegin() {
		primaryKeys.clear();
	}

	@Override
	protected void rowBegin() {
		hasPrimary = false;
	}

	@Override
	protected void excelFinsihCall() {}

}
