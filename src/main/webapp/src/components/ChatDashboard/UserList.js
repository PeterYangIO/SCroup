import React, { Component } from 'react';
import { withStyles } from '@material-ui/core/styles';
import Typography from '@material-ui/core/Typography';
import List from '@material-ui/core/List';
import ListItem from '@material-ui/core/ListItem';
import ListItemText from '@material-ui/core/ListItemText';
import Paper from '@material-ui/core/Paper';

const styles = theme => ({
    leftContainer: {
        marginTop: 20,
        marginBottom: 30
    },
    title: {
        textAlign: 'center'
    },
    users: {
        textAlign: 'center'
    },
    overflow: {
        height: 170,
        overflowY: 'scroll'
    }
});

class UserList extends Component {
    render() {
        const { classes } = this.props;
        return (
            <Paper className={classes.leftContainer}>
                <div>
                    <Typography variant="h6" className={classes.title}>
                        Group Members
                    </Typography>
                </div>
                <div className={classes.overflow}>
                    <List>
                        <ListItem dense button>
                            <ListItemText className={classes.users}>Nhan Tran</ListItemText>
                        </ListItem>
                        <ListItem dense button>
                            <ListItemText className={classes.users}>Marisa Eng</ListItemText>
                        </ListItem>
                        <ListItem dense button>
                            <ListItemText className={classes.users}>Catherine Mai</ListItemText>
                        </ListItem>
                        <ListItem dense button>
                            <ListItemText className={classes.users}>Jesus Ojeda</ListItemText>
                        </ListItem>
                    </List>
                </div>
            </Paper>
        );
    }
}


export default withStyles(styles)(UserList);
