import React from 'react'
import './iconfont.css'

function createIcon(code) {
  return <span className="iconfont erp-icons" dangerouslySetInnerHTML={{ __html: code }}></span>
}
const MainIcon = createIcon('&#xe610;')
const AnnualPlanningMeetingIcon = createIcon('&#xe601;')
const MarketManagementIcon = createIcon('&#xe828;')
const ProductionManagementIcon = createIcon('&#xe602;')
const SellManagementIcon = createIcon('&#xe653;')
const PurchaseManagementIcon = createIcon('&#xe600;')
const InventoryManagementIcon = createIcon('&#xe605;')
const RDIcon = createIcon('&#xe63e;')
const FinancialManagementIcon = createIcon('&#xe60e;')
const TaxesIcon = createIcon('&#xe693;')
const LoanIcon = createIcon('&#xe690;')
const TimeIcon = createIcon('&#xea41;')
const OverIcon = createIcon('&#xe686;')
export {
  MainIcon,
  AnnualPlanningMeetingIcon,
  MarketManagementIcon,
  ProductionManagementIcon,
  SellManagementIcon,
  PurchaseManagementIcon,
  InventoryManagementIcon,
  RDIcon,
  FinancialManagementIcon,
  TaxesIcon,
  LoanIcon,
  TimeIcon,
  OverIcon,
}
