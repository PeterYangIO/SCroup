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
    constructor(props) {
        super(props);

        this.state = {
            users: []
        }
    }

    componentDidMount() {
        this.getUsers();
    }

    getUsers = () => {
        fetch("http://localhost:8080/api/join-group?groupId=" + this.props.groupId)
            .then(res => res.json())
            .then(
                (res) => {
                    console.log(res);
                    const users = [];
                    res.forEach(el => {
                        users.push({
                            name: el.firstName + ' ' + el.lastName
                        })
                    });
                    this.setState({
                        users: this.state.users.concat(users)
                    })
                }
            )
    };

    render() {
        const { classes } = this.props;
        console.log(this.state.users);
        return (
            <Paper className={classes.leftContainer}>
                <div>
                    <Typography variant="h6" className={classes.title}>
                        Group Members
                    </Typography>
                </div>
                <div className={classes.overflow}>
                    <List>
                        {this.state.users.map((el, id)=> {
                            return(
                                <ListItem dense key={id}>
                                    <ListItemText className={classes.users}>{el.name}</ListItemText>
                                </ListItem>
                            );
                        })}
                    </List>
                </div>
            </Paper>
        );
    }
}


export default withStyles(styles)(UserList);
