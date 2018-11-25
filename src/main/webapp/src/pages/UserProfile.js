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
    render() {
        const { classes } = this.props;
        return (
            <div>
                <HeaderBar title="Login" />
                <Grid container alignContent='center' className={classes.root}>
                    <Grid item sm={4}>
                        <UserInfo/>
                    </Grid>
                    <Grid item sm={8}>
                        <ClassList/>
                        <GroupList/>
                    </Grid>
                </Grid>
            </div>
        );
    }
}


export default withStyles(styles)(UserProfile);

