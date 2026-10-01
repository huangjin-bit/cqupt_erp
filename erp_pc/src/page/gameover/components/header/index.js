import React, { Fragment } from 'react'

import './index.scss'
import logoUrl from 'assets/images/startGame_logo.jpg'

function Header() {
    return (
        <Fragment>
            <div className="game-operate-header-wrapper">
                <img src={logoUrl} />
            </div>
        </Fragment>
    )
}
export default Header
