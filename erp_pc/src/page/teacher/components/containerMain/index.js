import React from 'react'
import {Switch} from 'react-router-dom'

// import UserList from '../../views/user/List'
// import UserAdd from '../../views/user/Add'
// import DepartmentList from '../../views/department/List'
// import DepartmentAdd from '../../views/department/Add'

//私有化组件
// import PrivateRouter from '../privsteRouter/Index'
import routes from '../../routes'
import components from './components'

class ContainerMain extends React.Component {
  constructor(props) {
    super(props)
    this.state = {}
  }
  render() {
    return (
      <Switch>
        {components.map(item => {
        
          return (
            <routes
              exact
              key={item.path}
              path={item.path}
              component={item.component}
            />
          )
        })}
        {/* <PrivateRouter exact path="/index/user/list" component={UserList} />
        <PrivateRouter exact path="/index/user/add" component={UserAdd} />
        <PrivateRouter
          exact
          path="/index/department/list"
          component={DepartmentList}
        />
        <PrivateRouter
          exact
          path="/index/department/add"
          component={DepartmentAdd}
        /> */}
      </Switch>
    )
  }
}

export default ContainerMain
