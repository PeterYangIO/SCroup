import React from "react";
import HeaderBar from "../components/common/HeaderBar";
import { withStyles } from '@material-ui/core/styles';
import Grid from '@material-ui/core/Grid';
import UserList from '../components/ChatDashboard/UserList';
import Events from '../components/ChatDashboard/Events'
import ChatMessage from '../components/ChatDashboard/Chat';

const styles = theme => ({
    root: {
        flexGrow: 1,
    }
});

class GroupDashboard extends React.Component {
    render() {
        const { classes } = this.props;
        return (
            <>
                <HeaderBar title="Login"/>
                <div className={classes.root}>
                    <Grid container spacing={8}>
                        <Grid item xs={2} sm={4}>
                            <UserList />
                            <Events />
                        </Grid>
                        <Grid item xs={6} sm={8}>
                            <ChatMessage />
                        </Grid>
                    </Grid>
                </div>
            </>
        );
    }
}

export default withStyles(styles)(GroupDashboard);
