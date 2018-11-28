import React from "react";
import { withStyles } from "@material-ui/core/styles";
import Grid from "@material-ui/core/Grid";
import HeaderBar from "../components/common/HeaderBar";
import ClassList from "../components/UserProfile/ClassList";
import GroupList from "../components/UserProfile/GroupList";
import UserInfo from "../components/UserProfile/UserInfo";


const styles = theme => ({
    root: {
        flexGrow: 1,
        maxWidth: 1200,
        marginLeft: 'auto',
        marginRight: 'auto',
        marginTop: 20
    }
});

class UserProfile extends React.Component {

    constructor(props) {
        super(props);
        this.state = {
            info : {}
        }
    }

    componentDidMount() {
        this.getGroup();
    }

    getGroup = () => {
        console.log("here");
        fetch("http://localhost:8080/api/study-groups?GetGroupInfo=" + JSON.parse(sessionStorage.getItem("user")).id)
            .then(res => res.json())
            .then(
                (res) => {
                    this.setState({
                        info : res
                    })
                }
            )
    };

    render() {
        const { classes } = this.props;
        console.log(this.state.info);
        return (
            <div>
                <HeaderBar title="Login" />
                <Grid container alignContent='center' className={classes.root}>
                    <Grid item sm={4}>
                        <UserInfo/>
                    </Grid>
                    <Grid item sm={8}>
                        <ClassList info={this.state.info}/>
                        <GroupList info={this.state.info}/>
                    </Grid>
                </Grid>
            </div>
        );
    }
}


export default withStyles(styles)(UserProfile);

