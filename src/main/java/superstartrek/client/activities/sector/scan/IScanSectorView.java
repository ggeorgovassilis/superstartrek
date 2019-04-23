package superstartrek.client.activities.sector.scan;

import superstartrek.client.activities.IBaseView;

public interface IScanSectorView extends IBaseView<ScanSectorPresenter>{

	void setObjectName(String value);

	void setObjectLocation(String value);

	void setObjectQuadrant(String value);

	void setProperty(String rowId, String cellId, String rowCss, String value);

}