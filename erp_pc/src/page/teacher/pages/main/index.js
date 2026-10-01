import React from 'react'
import { useHistory } from 'react-router-dom'
import styles from './index.module.scss'

import Erpimg from 'assets/flash/erp.png'
import CarImg1 from 'assets/flash/car.png'
import CarImg2 from 'assets/flash/car2.png'

function Main() {
  let history = useHistory()
  const redirect = (url) => {
    history.push(url)
  }

  return (
    <div className={styles["userprepare-wrapper"]}>
      <div id="container">
        <img src={Erpimg} alt="" usemap="#Map" id="image" />
        <img src={CarImg1} alt="" class="car" id="car1" />
        <img src={CarImg2} alt="" class="car" id="car2" />
      </div>
      <map name="Map" id="Map">
        <area
          shape="poly"
          coords="236,25,185,57,186,113,237,139,289,110,288,57"
          target="iframebody"
          //   onClick={redirect.bind(null, '/gameoperate/checkJournalEntry')}
          alt="财务中心"
          data-name="财务中心"
        />
        <area
          shape="poly"
          coords="77,137,3,180,3,254,229,386,300,342,295,268"
          //   onClick={redirect.bind(null, '/gameoperate/productResearch')}
          target="iframebody"
          alt="研究中心"
          data-name="研究中心"
        />
        <area
          shape="poly"
          coords="456,298,397,332,359,444,357,504,436,501,494,468,494,325"
          //   onClick={redirect.bind(null, '/gameoperate/applyLoan')}
          target="iframebody"
          alt="银行"
          data-name="银行"
        />
        <area
          shape="poly"
          coords="228,511,146,538,143,634,228,669,303,635,302,538"
          //   onClick={redirect.bind(null, '/gameoperate/marketManagement')}
          alt="市场"
          data-name="市场"
        />
        <area
          shape="poly"
          coords="568,483,550,668,600,694,656,662,649,520"
          //   onClick={redirect.bind(null, '/gameoperate/materialPurchase')}
          target="iframebody"
          alt="采购中心"
          data-name="采购中心"
        />
        <area
          shape="poly"
          coords="732,442,666,521,674,573,794,573,795,511"
          //   onClick={redirect.bind(null, '/gameoperate/productInventory')}
          target="iframebody"
          alt="仓库"
          data-name="仓库"
        />
        <area
          shape="poly"
          coords="911,547,872,575,872,707,948,735,948,602"
          //   onClick={redirect.bind(null, '/gameoperate/taxes')}
          target="iframebody"
          alt="税务局"
          data-name="税务局"
        />
        <area
          shape="poly"
          coords="800,112,533,242,528,311,596,346,862,197,865,145"
          //   onClick={redirect.bind(null, '/gameoperate/workshopManagement')}
          target="iframebody"
          alt="生产中心"
          data-name="生产中心"
        />
      </map>
    </div>
  )
}

export default Main
